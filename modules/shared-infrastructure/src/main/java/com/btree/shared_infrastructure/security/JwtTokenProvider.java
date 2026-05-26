package com.btree.shared_infrastructure.security;

import com.btree.shared.contract.TokenProvider;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.text.ParseException;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Component
public class JwtTokenProvider implements TokenProvider {

    private static final Set<String> RESERVED_CLAIMS = Set.of(
            "iss", "sub", "aud", "exp", "nbf", "iat", "jti"
    );

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final SecretKey jwtSigningKey;
    private final JwtProperties properties;

    public JwtTokenProvider(
            final JwtEncoder jwtEncoder,
            final JwtDecoder jwtDecoder,
            final SecretKey jwtSigningKey,
            final JwtProperties properties
    ) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.jwtSigningKey = jwtSigningKey;
        this.properties = properties;
    }

    @Override
    public String generate(final String subject, final Map<String, Object> claims, final Instant expiresAt) {
        final var now = Instant.now();
        final var claimsBuilder = JwtClaimsSet.builder()
                .issuer(properties.getIssuer())
                .subject(subject)
                .issuedAt(now)
                .expiresAt(expiresAt);

        if (claims != null) {
            claims.forEach((key, value) -> {
                if (!RESERVED_CLAIMS.contains(key)) {
                    claimsBuilder.claim(key, value);
                }
            });
        }

        final var headers = JwsHeader.with(MacAlgorithm.HS256)
                .type("JWT")
                .build();

        return jwtEncoder
                .encode(JwtEncoderParameters.from(headers, claimsBuilder.build()))
                .getTokenValue();
    }

    @Override
    public String extractSubject(final String token) {
        return verifiedClaims(token).getSubject();
    }

    @Override
    public boolean isValid(final String token) {
        try {
            jwtDecoder.decode(token);
            return true;
        } catch (final JwtException ex) {
            return false;
        }
    }

    @Override
    public <T> T extractClaim(final String token, final String claimKey, final Class<T> type) {
        final var value = verifiedClaims(token).getClaim(claimKey);
        if (value == null) {
            return null;
        }
        if (!type.isInstance(value)) {
            throw new JwtException("Claim '%s' não é do tipo %s".formatted(claimKey, type.getSimpleName()));
        }
        return type.cast(value);
    }

    private JWTClaimsSet verifiedClaims(final String token) {
        try {
            final var signedJwt = SignedJWT.parse(token);
            final var verifier = new MACVerifier(jwtSigningKey.getEncoded());
            if (!signedJwt.verify(verifier)) {
                throw new JwtException("Assinatura JWT inválida");
            }
            return signedJwt.getJWTClaimsSet();
        } catch (final ParseException | JOSEException ex) {
            throw new JwtException("JWT inválido", ex);
        }
    }
}
