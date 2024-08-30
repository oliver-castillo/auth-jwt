package com.app.auth.service.impl.jwt;

import com.app.auth.util.exception.JwtException;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.authentication.TokenCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final String issuer = System.getenv("JWT_ISSUER");
    private final String audience = System.getenv("JWT_AUDIENCE");
    private final JwtConfig jwtConfig;

    private String buildToken(String subject, Map<String, Object> extraClaims) {
        return jwtConfig.jwtAuth().generateToken(
                new JsonObject(extraClaims),
                new JWTOptions()
                        .setAlgorithm("RS256")
                        .setSubject(subject)
                        .setIssuer(issuer)
                        .setExpiresInMinutes(60)
                        .setAudience(List.of(audience)));
    }

    public String generateToken(String subject) {
        return buildToken(subject, new HashMap<>());
    }

    public String generateToken(String subject, Map<String, Object> claims) {
        return buildToken(subject, claims);
    }

    private JsonObject extractAllClaims(String jwt) {
        TokenCredentials credentials = new TokenCredentials(jwt);
        AtomicReference<JsonObject> claims = new AtomicReference<>(new JsonObject());
        jwtConfig.jwtAuth().authenticate(credentials)
                .onSuccess(user ->
                        claims.set(user.principal())
                )
                .onFailure(err -> {
                    throw new JwtException("Error extracting claims from token", err);
                });
        return claims.get();
    }

    protected String getSubject(String jwt) {
        return extractAllClaims(jwt).getString("sub");
    }

    protected boolean verifyToken(String token) {
        return !extractAllClaims(token).isEmpty();
    }
}
