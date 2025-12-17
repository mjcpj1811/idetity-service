package com.example.identity_service.configuration;

import java.nio.charset.StandardCharsets;
import javax.crypto.spec.SecretKeySpec;

import com.example.identity_service.repository.InvalidatedTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;



@Component
public class CustomJwtDecoder implements JwtDecoder {

    private final InvalidatedTokenRepository invalidatedTokenRepository;
    private final NimbusJwtDecoder nimbusJwtDecoder;

    public CustomJwtDecoder(
            InvalidatedTokenRepository invalidatedTokenRepository,
            @Value("${jwt.signing-key}") String key
    ) {
        this.invalidatedTokenRepository = invalidatedTokenRepository;

        SecretKeySpec secretKey = new SecretKeySpec(
                key.getBytes(StandardCharsets.UTF_8),
                "HS256"
        );

        this.nimbusJwtDecoder = NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        Jwt jwt = nimbusJwtDecoder.decode(token);

        String jti = jwt.getClaimAsString("jti");
        if (jti != null && invalidatedTokenRepository.existsById(jti)) {
            throw new JwtException("Token has been revoked");
        }

        return jwt;
    }
}
