package com.app.auth.service.impl.jwt;

import com.app.auth.util.exception.InternalErrorException;
import io.fusionauth.jwt.Signer;
import io.fusionauth.jwt.Verifier;
import io.fusionauth.jwt.domain.JWT;
import io.fusionauth.jwt.rsa.RSASigner;
import io.fusionauth.jwt.rsa.RSAVerifier;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Base64;
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
        Signer signer = RSASigner.newSHA256Signer(getPrivateKey());
        JWT jwt = new JWT();
        jwt.setSubject(subject);
        jwt.setIssuer(issuer);
        jwt.setAudience(audience);
        jwt.setExpiration(ZonedDateTime.now(ZoneOffset.UTC).plusHours(1));
        extraClaims.forEach(jwt::addClaim);
        return JWT.getEncoder().encode(jwt, signer);
    }

    public String generateToken(String subject) {
        return buildToken(subject, new HashMap<>());
    }

    public String generateToken(String subject, Map<String, Object> claims) {
        return buildToken(subject, claims);
    }

    private JWT extractAllClaims(String jwt) {
        Verifier verifier = RSAVerifier.newVerifier(getPublicKey());
        return JWT.getDecoder().decode(jwt, verifier);
    }

    protected String getSubject(String jwt) {
        return extractAllClaims(jwt).subject;
    }

    protected boolean verifyToken(String token) {
        try {
            Verifier verifier = RSAVerifier.newVerifier(getPublicKey());
            JWT jwt = JWT.getDecoder().decode(token, verifier);

            ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);

            return (jwt.expiration != null && !jwt.expiration.isBefore(now)) &&
                    (jwt.issuer != null && jwt.issuer.equals(issuer)) &&
                    (jwt.audience != null && jwt.audience.equals(audience));

        } catch (Exception e) {
            return false;
        }
    }

}
