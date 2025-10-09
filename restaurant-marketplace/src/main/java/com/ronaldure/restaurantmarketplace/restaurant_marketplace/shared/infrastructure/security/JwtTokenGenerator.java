package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
public class JwtTokenGenerator {

    private final byte[] secret;
    private final String issuer;
    private final String audience;
    private final long accessTtlMinutes;

    public JwtTokenGenerator(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.issuer}") String issuer,
            @Value("${jwt.audience}") String audience,
            @Value("${jwt.accessTokenTtlMinutes:15}") long accessTtlMinutes
    ) {
        this.secret = Objects.requireNonNull(secret, "jwt.secret").getBytes(StandardCharsets.UTF_8);
        this.issuer = Objects.requireNonNull(issuer, "jwt.issuer");
        this.audience = Objects.requireNonNull(audience, "jwt.audience");
        this.accessTtlMinutes = accessTtlMinutes;
    }

    public String generate(String userId, String email, List<Role> roles, Long tenantId) {
        try {
            Instant now = Instant.now();
            Instant exp = now.plus(accessTtlMinutes, ChronoUnit.MINUTES);

            JWTClaimsSet.Builder b = new JWTClaimsSet.Builder()
                    .subject(userId)
                    .issuer(issuer)
                    .audience(audience)
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(exp))
                    .claim("roles", roles.stream().map(Enum::name).toList());

            if (email != null && !email.isBlank()) {
                b.claim("email", email);
            }
            
            if (tenantId != null) {
                b.claim("tenantId", tenantId);
            }

            JWTClaimsSet claims = b.build();
            JWSSigner signer = new MACSigner(secret);
            SignedJWT jwt = new SignedJWT(
                    new com.nimbusds.jose.JWSHeader.Builder(JWSAlgorithm.HS256).type(com.nimbusds.jose.JOSEObjectType.JWT).build(),
                    claims
            );
            jwt.sign(signer);
            return jwt.serialize();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate JWT", e);
        }
    }
}
