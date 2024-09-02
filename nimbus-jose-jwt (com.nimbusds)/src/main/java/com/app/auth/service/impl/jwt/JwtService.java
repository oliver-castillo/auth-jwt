package com.app.auth.service.impl.jwt;

import com.app.auth.util.exception.InternalErrorException;
import com.app.auth.util.exception.JwtException;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
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
        try {
            JWSSigner signer = new RSASSASigner(getPrivateKey());
            JWTClaimsSet.Builder claimsSetBuilder = new JWTClaimsSet.Builder();
            JWTClaimsSet claimsSet = claimsSetBuilder
                    .subject(subject)
                    .issuer(issuer)
                    .audience(audience)
                    .expirationTime(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                    .build();
            for (Map.Entry<String, Object> entry : extraClaims.entrySet()) {
                claimsSetBuilder.claim(entry.getKey(), entry.getValue());
            }
            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256).build(), claimsSet);
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new JwtException("Error creating token", e);
        }
    }

    public String generateToken(String subject) {
        return buildToken(subject, new HashMap<>());
    }

    public String generateToken(String subject, Map<String, Object> claims) {
        return buildToken(subject, claims);
    }

    protected boolean verifyTokenAndGetClaims(String jwt) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(jwt);
            JWSVerifier verifier = new RSASSAVerifier(getPublicKey());
            boolean isValid = signedJWT.verify(verifier);
            boolean isExpired = signedJWT.getJWTClaimsSet().getExpirationTime().before(new Date());
            boolean isIssuerValid = signedJWT.getJWTClaimsSet().getIssuer().equals(issuer);
            boolean isAudienceValid = signedJWT.getJWTClaimsSet().getAudience().contains(audience);
            return isValid && !isExpired && isIssuerValid && isAudienceValid;
        } catch (ParseException | JOSEException e) {
            throw new JwtException("Error verifying token", e);
        }
    }

    protected String getSubject(String jwt) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(jwt);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            throw new JwtException("Error verifying token", e);
        }
    }


}
