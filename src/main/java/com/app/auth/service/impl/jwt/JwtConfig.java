package com.app.auth.service.impl.jwt;

import com.app.auth.util.exception.InternalErrorException;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    @Bean
    public JWTAuth jwtAuth() {
        Vertx vertx = Vertx.vertx();
        JWTAuthOptions config = new JWTAuthOptions()
                .addPubSecKey(new PubSecKeyOptions()
                        .setAlgorithm("RS256")
                        .setBuffer(getPrivateKeyPEM()))
                .addPubSecKey(new PubSecKeyOptions()
                        .setAlgorithm("RS256")
                        .setBuffer(getPublicKeyPEM()));
        return JWTAuth.create(vertx, config);
    }

    private String getPublicKeyPEM() {
        try {
            return System.getenv("PUBLIC_KEY")
                    .replace("-----BEGIN PUBLIC KEY----- ", "-----BEGIN_PUBLIC_KEY-----\n")
                    .replace(" -----END PUBLIC KEY-----", "\n-----END_PUBLIC_KEY-----")
                    .replace(" ", "\n")
                    .replace("_", " ");
        } catch (Exception e) {
            throw new InternalErrorException("Error while getting public key", e);
        }
    }

    private String getPrivateKeyPEM() {
        try {
            return System.getenv("PRIVATE_KEY")
                    .replace("-----BEGIN PRIVATE KEY----- ", "-----BEGIN_PRIVATE_KEY-----\n")
                    .replace(" -----END PRIVATE KEY-----", "\n-----END_PRIVATE_KEY-----")
                    .replace(" ", "\n")
                    .replace("_", " ");
        } catch (Exception e) {
            throw new InternalErrorException("Error while getting private key", e);
        }
    }
}
