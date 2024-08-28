package com.app.auth.service.impl.jwt;

import com.app.auth.util.exception.InternalErrorException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private final String issuer = System.getenv("JWT_ISSUER");
    private final String audience = System.getenv("JWT_AUDIENCE");

    private RSAPublicKey getPublicKey() {
        try {
            String publicKey = System.getenv("PUBLIC_KEY")
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replace("\n", "")
                    .replaceAll("\\s", "");
            byte[] decodedKey = Base64.getDecoder().decode(publicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new InternalErrorException("Error setting up public key", e);
        }
    }

    private RSAPrivateKey getPrivateKey() {
        try {
            String privateKey = System.getenv("PRIVATE_KEY")
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replace("\n", "")
                    .replaceAll("\\s", "");
            byte[] decodedKey = Base64.getDecoder().decode(privateKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new InternalErrorException("Error setting up private key", e);
        }
    }

    private String buildToken(String subject, Map<String, Object> extraClaims) {
        final long expiration = System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7;
        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuer(issuer)
                .issuedAt(new Date())
                .audience().add(audience).and()
                .expiration(new Date(expiration))
                .signWith(getPrivateKey())
                .compact();
    }

    public String generateToken(String subject) {
        return buildToken(subject, new HashMap<>());
    }

    public String generateToken(String subject, Map<String, Object> claims) {
        return buildToken(subject, claims);
    }

    private Claims extractAllClaims(String jwt) {
        try {
            return Jwts.parser()
                    .verifyWith(getPublicKey())
                    .requireIssuer(issuer)
                    .requireAudience(audience)
                    .build()
                    .parseSignedClaims(jwt).getPayload();
        } catch (JwtException e) {
            throw new com.app.auth.util.exception.JwtException("Error verifying token", e);
        }
    }

    protected String getSubject(String jwt) {
        return extractAllClaims(jwt).getSubject();
    }

    protected boolean verifyToken(String token) {
        return extractAllClaims(token) != null;
    }


}
