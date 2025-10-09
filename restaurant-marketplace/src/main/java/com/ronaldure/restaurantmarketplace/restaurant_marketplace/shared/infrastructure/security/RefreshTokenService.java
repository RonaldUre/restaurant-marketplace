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
import java.util.*;

@Service
public class RefreshTokenService {

    private static final String SUBJECT_ADMIN = "ADMIN";
    private static final String SUBJECT_CUSTOMER = "CUSTOMER";

    private final RefreshTokenCodec refreshCodec;
    private final RefreshTokenJpaRepository refreshRepo;
    private final UserAuthJpaRepository usersRepo;
    private final JwtTokenGenerator accessTokenGenerator; // reuse access token generator

    public record TokensPair(String accessToken, String refreshToken) {}

    public RefreshTokenService(RefreshTokenCodec refreshCodec,
                               RefreshTokenJpaRepository refreshRepo,
                               // kept for DI compatibility, not used here
                               UserAuthJpaRepository usersRepo,
                               JwtTokenGenerator accessTokenGenerator) {
        this.refreshCodec = refreshCodec;
        this.refreshRepo = refreshRepo;
        this.accessTokenGenerator = accessTokenGenerator;
        this.usersRepo = usersRepo;
    }

    /** Issues refresh+access at login and persists the RT with a hash and subject type. */
    @Transactional
    public TokensPair issueOnLogin(Long userId, String email, List<Role> roles, Long tenantId, String ip, String userAgent) {
        // 0) Decide subject type (namespace between admin and customer sessions)
        String subjectType = resolveSubjectType(roles);

        // 1) Generate refresh (with known jti to store hash)
        String jti = UUID.randomUUID().toString();
        String refresh = refreshCodec.generate(String.valueOf(userId), roles, tenantId, jti);

        // 2) Persist row with token hash
        JpaRefreshTokenEntity e = new JpaRefreshTokenEntity();
        e.setJti(jti);
        e.setUserId(userId);
        e.setSubjectType(subjectType);
        e.setTokenHash(Sha256.hex(refresh));
        e.setExpiresAt(toLdt(expFrom(refresh)));
        e.setRevoked(false);
        e.setReplacedByJti(null);
        e.setCreatedByIp(safe(ip, "unknown"));
        e.setUserAgent(safe(userAgent, "unknown"));
        refreshRepo.save(e);

        // 3) Generate short-lived access token
        String access = accessTokenGenerator.generate(String.valueOf(userId), email, roles, tenantId);

        return new TokensPair(access, refresh);
    }

    /**
     * Rotates a valid refresh ⇒ returns new access + new refresh.
     * Handles reuse: on reuse, revoke all active RTs for the same (userId, subjectType).
     */
    @Transactional
    public TokensPair rotate(String rawRefresh, String ip, String userAgent) {
        var claims = refreshCodec.parseAndValidate(rawRefresh);

        Long userId = Long.parseLong(claims.userId());
        String jti = claims.jti();

        var rowOpt = refreshRepo.findByJtiWithPessimisticLock(jti);
        if (rowOpt.isEmpty()) {
            throw new SecurityException("Refresh not recognized");
        }
        var row = rowOpt.get();

        // Exact hash check (defense-in-depth)
        String incomingHash = Sha256.hex(rawRefresh);
        if (!Objects.equals(row.getTokenHash(), incomingHash)) {
            throw new SecurityException("Refresh mismatch");
        }

        // Expiration and state
        if (row.isRevoked() || row.getReplacedByJti() != null) {
            // Reuse detected ⇒ revoke all active for this subject type
            revokeAllActiveForUser(userId, row.getSubjectType());
            throw new SecurityException("Refresh reuse detected; all sessions revoked");
        }
        if (row.getExpiresAt().isBefore(LocalDateTime.now(ZoneOffset.UTC))) {
            throw new SecurityException("Refresh expired");
        }

        // Roles and tenant from claims (we embedded them in the refresh)
        Set<Role> roles = EnumSet.noneOf(Role.class);
        for (String r : claims.roles()) roles.add(Role.valueOf(r));
        Long tenantId = null;
        try { tenantId = claims.tenantId(); } catch (Exception ignored) {}

         var user = usersRepo.findById(userId)
                .orElseThrow(() -> new SecurityException("User not found for refresh token: " + userId));

        // 1) Generate new refresh with a new jti
        String newJti = UUID.randomUUID().toString();
        String newRefresh = refreshCodec.generate(claims.userId(), List.copyOf(roles), tenantId, newJti);

        // 2) Persist new refresh, inherit subject type from current row
        var newRow = new JpaRefreshTokenEntity();
        newRow.setJti(newJti);
        newRow.setUserId(userId);
        newRow.setSubjectType(row.getSubjectType());
        newRow.setTokenHash(Sha256.hex(newRefresh));
        newRow.setExpiresAt(toLdt(expFrom(newRefresh)));
        newRow.setRevoked(false);
        newRow.setCreatedByIp(safe(ip, "unknown"));
        newRow.setUserAgent(safe(userAgent, "unknown"));
        refreshRepo.save(newRow);

        // 3) Mark current as rotated
        row.setRevoked(true);
        row.setReplacedByJti(newJti);
        refreshRepo.save(row);

        // 4) Issue new access token
        String newAccess = accessTokenGenerator.generate(String.valueOf(userId),user.getEmail(), List.copyOf(roles), tenantId);

        return new TokensPair(newAccess, newRefresh);
    }

    /** Revokes all active refresh tokens for the (userId, subjectType) tuple (used on reuse). */
    @Transactional
    public void revokeAllActiveForUser(Long userId, String subjectType) {
        var list = refreshRepo.findAllByUserIdAndSubjectTypeAndRevokedFalse(userId, subjectType);
        for (var e : list) {
            e.setRevoked(true);
            e.setReplacedByJti(null);
        }
        refreshRepo.saveAll(list);
    }

    @Transactional
    public void logout(String rawRefresh, boolean revokeAllSessions) {
        // 0) Parse claims; if invalid/expired, treat logout as success (idempotent)
        RefreshTokenCodec.RefreshClaims claims;
        try {
            claims = refreshCodec.parseAndValidate(rawRefresh);
        } catch (SecurityException ex) {
            return; // nothing to revoke in DB
        }

        Long userId = Long.parseLong(claims.userId());
        String jti = claims.jti();

        var rowOpt = refreshRepo.findByJti(jti);
        if (rowOpt.isEmpty()) {
            return; // already cleaned or unknown ⇒ idempotent
        }
        var row = rowOpt.get();

        // Exact hash check (defense)
        String incomingHash = Sha256.hex(rawRefresh);
        if (!Objects.equals(row.getTokenHash(), incomingHash)) {
            return; // treat as no-op
        }

        // Already revoked/rotated ⇒ no-op
        if (row.isRevoked() || row.getReplacedByJti() != null) {
            return;
        }

        if (revokeAllSessions) {
            revokeAllActiveForUser(userId, row.getSubjectType());
        } else {
            row.setRevoked(true);
            row.setReplacedByJti(null);
            refreshRepo.save(row);
        }
    }

    // ----------------- helpers -----------------

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

    /** Derives subject type from roles to namespace sessions. */
    private static String resolveSubjectType(List<Role> roles) {
        // If the token has CUSTOMER, treat it as a customer session; otherwise it's an admin session.
        boolean isCustomer = roles != null && roles.stream().anyMatch(r -> r == Role.CUSTOMER);
        return isCustomer ? SUBJECT_CUSTOMER : SUBJECT_ADMIN;
    }

    private static String safe(String v, String def) {
        return (v == null || v.isBlank()) ? def : v;
    }
}
