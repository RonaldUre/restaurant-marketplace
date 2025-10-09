package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class RefreshTokenCodec {

    private final byte[] secret;
    private final String issuer;
    private final String audience;
    private final long ttlDays;

    public record RefreshClaims(
            String userId,
            String jti,
            List<String> roles,
            Long tenantId,
            Instant expiresAt
    ) {}

    public RefreshTokenCodec(
            @Value("${refresh.jwt.secret}") String secret,
            @Value("${refresh.jwt.issuer}") String issuer,
            @Value("${refresh.jwt.audience}") String audience,
            @Value("${refresh.jwt.ttlDays:15}") long ttlDays
    ) {
        this.secret = Objects.requireNonNull(secret, "refresh.jwt.secret").getBytes(StandardCharsets.UTF_8);
        this.issuer = Objects.requireNonNull(issuer, "refresh.jwt.issuer");
        this.audience = Objects.requireNonNull(audience, "refresh.jwt.audience");
        this.ttlDays = ttlDays;
    }

    public String generate(String userId, List<Role> roles, Long tenantId, String jtiOverride) {
        try {
            Instant now = Instant.now();
            Instant exp = now.plus(ttlDays, ChronoUnit.DAYS);
            String jti = (jtiOverride != null) ? jtiOverride : UUID.randomUUID().toString();

            JWTClaimsSet.Builder b = new JWTClaimsSet.Builder()
                    .subject(userId)
                    .issuer(issuer)
                    .audience(audience)
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(exp))
                    .jwtID(jti)
                    .claim("roles", roles.stream().map(Enum::name).toList())
                    .claim("token_use", "refresh");

            if (tenantId != null) b.claim("tenantId", tenantId);

            JWTClaimsSet claims = b.build();

            JWSSigner signer = new MACSigner(secret);
            SignedJWT jwt = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWT).build(),
                    claims
            );
            jwt.sign(signer);
            return jwt.serialize();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate refresh JWT", e);
        }
    }

    public RefreshClaims parseAndValidate(String raw) {
        try {
            SignedJWT jwt = SignedJWT.parse(raw);
            if (!JWSAlgorithm.HS256.equals(jwt.getHeader().getAlgorithm())) {
                throw new SecurityException("Unsupported refresh alg");
            }
            JWSVerifier verifier = new MACVerifier(secret);
            if (!jwt.verify(verifier)) throw new SecurityException("Invalid refresh signature");

            JWTClaimsSet c = jwt.getJWTClaimsSet();

            if (!issuer.equals(c.getIssuer())) throw new SecurityException("Invalid issuer");
            List<String> aud = c.getAudience();
            if (aud == null || aud.stream().noneMatch(audience::equals)) throw new SecurityException("Invalid audience");
            if (!"refresh".equals(c.getStringClaim("token_use"))) throw new SecurityException("Invalid token_use");

            Date exp = c.getExpirationTime();
            if (exp == null || Instant.now().isAfter(exp.toInstant())) throw new SecurityException("Refresh expired");

            String sub = c.getSubject();
            if (sub == null || sub.isBlank()) throw new SecurityException("Missing sub");
            String jti = c.getJWTID();
            if (jti == null || jti.isBlank()) throw new SecurityException("Missing jti");

            @SuppressWarnings("unchecked")
            List<String> roles = Optional.ofNullable((List<String>) c.getClaim("roles")).orElse(List.of());
            Long tenantId = null;
            Object t = c.getClaim("tenantId");
            if (t instanceof Number n) tenantId = n.longValue();
            else if (t instanceof String s) tenantId = Long.parseLong(s);

            return new RefreshClaims(sub, jti, roles, tenantId, exp.toInstant());
        } catch (SecurityException se) {
            throw se;
        } catch (Exception e) {
            throw new SecurityException("Malformed/invalid refresh token", e);
        }
    }
}
