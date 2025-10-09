package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.TokenDecoder;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.AuthenticatedUser;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.Role;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.UserId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Clock;
import java.time.Instant;
import java.util.*;

@Component
public class NimbusTokenDecoder implements TokenDecoder {

    private final byte[] secret;
    private final String issuer;
    private final String audience;
    private final long clockSkewSeconds;
    private final Clock clock;

    public NimbusTokenDecoder(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.issuer}") String issuer,
            @Value("${jwt.audience}") String audience,
            @Value("${jwt.clockSkewSeconds:60}") long clockSkewSeconds
    ) {
        this.secret = Objects.requireNonNull(secret, "jwt.secret").getBytes(StandardCharsets.UTF_8);
        this.issuer = Objects.requireNonNull(issuer, "jwt.issuer");
        this.audience = Objects.requireNonNull(audience, "jwt.audience");
        this.clockSkewSeconds = clockSkewSeconds;
        this.clock = Clock.systemUTC();
    }

    @Override
    public AuthenticatedUser decode(String rawJwt) {
        try {
            SignedJWT jwt = SignedJWT.parse(rawJwt);
            JWSVerifier verifier = new MACVerifier(secret);
            if (!jwt.verify(verifier)) throw new SecurityException("Invalid JWT signature");
            if (!JWSAlgorithm.HS256.equals(jwt.getHeader().getAlgorithm())) {
                throw new SecurityException("Unsupported JWT alg");
            }

            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            validateRegisteredClaims(claims);

            String sub = requiredString(claims, "sub");
            UserId userId = new UserId(sub);

            @SuppressWarnings("unchecked")
            List<String> rolesRaw = Optional.ofNullable((List<String>) claims.getClaim("roles"))
                    .orElse(Collections.emptyList());
            if (rolesRaw.isEmpty()) throw new SecurityException("roles claim missing/empty");

            EnumSet<Role> roles = EnumSet.noneOf(Role.class);
            for (String r : rolesRaw) roles.add(Role.valueOf(r));

            // tenantId is optional; present for RESTAURANT_ADMIN tokens
            Long tenantId = null;
            Object t = claims.getClaim("tenantId");
            if (t != null) {
                if (t instanceof Number n) tenantId = n.longValue();
                else if (t instanceof String s) tenantId = Long.parseLong(s);
                else throw new SecurityException("tenantId claim must be number/string");
            }

            return new AuthenticatedUser(userId, roles, tenantId == null ? null : new com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId(tenantId));
        } catch (ParseException e) {
            throw new SecurityException("Malformed JWT", e);
        } catch (IllegalArgumentException e) {
            // Role.valueOf or parse errors
            throw new SecurityException("Invalid roles/tenantId in JWT", e);
        } catch (Exception e) {
            if (e instanceof SecurityException) throw (SecurityException) e;
            throw new SecurityException("JWT decode failure", e);
        }
    }

    private void validateRegisteredClaims(JWTClaimsSet claims) throws ParseException {
        // issuer
        if (issuer != null && !issuer.equals(claims.getIssuer())) {
            throw new SecurityException("Invalid issuer");
        }
        // audience
        if (audience != null) {
            List<String> aud = claims.getAudience();
            if (aud == null || aud.stream().noneMatch(audience::equals)) {
                throw new SecurityException("Invalid audience");
            }
        }
        // expiration / not before / issued at
        Instant now = clock.instant();
        Date exp = claims.getExpirationTime();
        if (exp == null) throw new SecurityException("Missing exp");
        if (now.isAfter(exp.toInstant().plusSeconds(clockSkewSeconds))) {
            throw new SecurityException("Token expired");
        }
        Date nbf = claims.getNotBeforeTime();
        if (nbf != null && now.isBefore(nbf.toInstant().minusSeconds(clockSkewSeconds))) {
            throw new SecurityException("Token not active yet");
        }
    }

    private static String requiredString(JWTClaimsSet claims, String name) throws ParseException {
        String v = claims.getStringClaim(name);
        if (v == null || v.isBlank()) throw new SecurityException("Missing claim: " + name);
        return v;
    }
}
