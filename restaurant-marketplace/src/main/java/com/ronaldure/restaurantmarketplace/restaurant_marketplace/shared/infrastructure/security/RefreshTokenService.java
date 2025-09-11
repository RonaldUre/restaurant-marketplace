package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.security;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.Role;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.persistence.entity.JpaRefreshTokenEntity;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.persistence.repository.RefreshTokenJpaRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.persistence.repository.UserAuthJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenCodec refreshCodec;
    private final RefreshTokenJpaRepository refreshRepo;

    private final JwtTokenGenerator accessTokenGenerator; // reusa tu generador de access tokens

    public record TokensPair(String accessToken, String refreshToken) {}

    public RefreshTokenService(RefreshTokenCodec refreshCodec,
                               RefreshTokenJpaRepository refreshRepo,
                               UserAuthJpaRepository usersRepo,
                               JwtTokenGenerator accessTokenGenerator) {
        this.refreshCodec = refreshCodec;
        this.refreshRepo = refreshRepo;
        this.accessTokenGenerator = accessTokenGenerator;
    }

    /** Emite refresh+access en login. Guarda RT con hash. */
    @Transactional
    public TokensPair issueOnLogin(Long userId, List<Role> roles, Long tenantId, String ip, String userAgent) {
        // 1) Generar refresh (con jti conocido para guardar hash)
        String jti = UUID.randomUUID().toString();
        String refresh = refreshCodec.generate(String.valueOf(userId), roles, tenantId, jti);

        // 2) Persistir fila con hash del token
        JpaRefreshTokenEntity e = new JpaRefreshTokenEntity();
        e.setJti(jti);
        e.setUserId(userId);
        e.setTokenHash(Sha256.hex(refresh));
        e.setExpiresAt(toLdt(expFrom(refresh)));
        e.setRevoked(false);
        e.setReplacedByJti(null);
        e.setCreatedByIp(ip);
        e.setUserAgent(userAgent);
        refreshRepo.save(e);

        // 3) Generar access token corto
        String access = accessTokenGenerator.generate(String.valueOf(userId), roles, tenantId);

        return new TokensPair(access, refresh);
    }

    /** Rota un refresh válido ⇒ devuelve nuevo access + nuevo refresh. Maneja reuso. */
    @Transactional
    public TokensPair rotate(String rawRefresh, String ip, String userAgent) {
        var claims = refreshCodec.parseAndValidate(rawRefresh);

        Long userId = Long.parseLong(claims.userId());
        String jti = claims.jti();

        var rowOpt = refreshRepo.findByJti(jti);
        if (rowOpt.isEmpty()) {
            // Desconocido en BD: puede ser robado o viejo ⇒ denegar.
            throw new SecurityException("Refresh not recognized");
        }

        var row = rowOpt.get();

        // Comprobar hash exacto (defensa contra confusión de jti)
        String incomingHash = Sha256.hex(rawRefresh);
        if (!Objects.equals(row.getTokenHash(), incomingHash)) {
            throw new SecurityException("Refresh mismatch");
        }

        // Expiración y estado
        if (row.isRevoked() || row.getReplacedByJti() != null) {
            // REUSO detectado ⇒ revocar todos los RT activos del usuario (política simple y efectiva)
            revokeAllActiveForUser(userId);
            throw new SecurityException("Refresh reuse detected; all sessions revoked");
        }
        if (row.getExpiresAt().isBefore(LocalDateTime.now(ZoneOffset.UTC))) {
            throw new SecurityException("Refresh expired");
        }

        // Roles y tenant desde claims (los metimos en el refresh)
        Set<Role> roles = EnumSet.noneOf(Role.class);
        for (String r : claims.roles()) roles.add(Role.valueOf(r));
        Long tenantId = null;
        try {
            tenantId = claims.tenantId();
        } catch (Exception ignored) {}

        // 1) Generar nuevo refresh con nuevo jti
        String newJti = UUID.randomUUID().toString();
        String newRefresh = refreshCodec.generate(claims.userId(), List.copyOf(roles), tenantId, newJti);

        // 2) Guardar nuevo refresh
        var newRow = new JpaRefreshTokenEntity();
        newRow.setJti(newJti);
        newRow.setUserId(userId);
        newRow.setTokenHash(Sha256.hex(newRefresh));
        newRow.setExpiresAt(toLdt(expFrom(newRefresh)));
        newRow.setRevoked(false);
        newRow.setCreatedByIp(ip);
        newRow.setUserAgent(userAgent);
        refreshRepo.save(newRow);

        // 3) Marcar el actual como rotado
        row.setRevoked(true);
        row.setReplacedByJti(newJti);
        refreshRepo.save(row);

        // 4) Emitir nuevo access token
        String newAccess = accessTokenGenerator.generate(String.valueOf(userId), List.copyOf(roles), tenantId);

        return new TokensPair(newAccess, newRefresh);
    }

    /** Revoca todos los refresh tokens activos del usuario (se usa ante reuso). */
    @Transactional
    public void revokeAllActiveForUser(Long userId) {
        var list = refreshRepo.findAllByUserIdAndRevokedFalse(userId);
        for (var e : list) {
            e.setRevoked(true);
            e.setReplacedByJti(null);
        }
        refreshRepo.saveAll(list);
    }

    private static LocalDateTime toLdt(Instant i) {
        return LocalDateTime.ofInstant(i, ZoneOffset.UTC);
    }

    private static Instant expFrom(String jwt) {
        try {
            var parsed = com.nimbusds.jwt.SignedJWT.parse(jwt);
            return parsed.getJWTClaimsSet().getExpirationTime().toInstant();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot read exp from JWT", e);
        }
    }
}
