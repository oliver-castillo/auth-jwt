package com.app.auth.service.impl.jwt;

import com.app.auth.util.exception.InternalErrorException;
import com.app.auth.util.exception.JwtException;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
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
    try {
      //Set the claims
      JwtClaims claims = new JwtClaims();
      claims.setIssuer(issuer);
      claims.setAudience(audience);
      claims.setExpirationTimeMinutesInTheFuture(10);
      claims.setGeneratedJwtId();
      claims.setIssuedAtToNow();
      claims.setNotBeforeMinutesInThePast(2);
      claims.setSubject(subject);
      for (Map.Entry<String, Object> claim : extraClaims.entrySet()) {
        claims.setClaim(claim.getKey(), claim.getValue());
      }
      //Sign the claims
      JsonWebSignature jws = new JsonWebSignature();
      jws.setPayload(claims.toJson());
      jws.setKey(getPrivateKey());
      jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
      return jws.getCompactSerialization();
    } catch (JoseException e) {
      throw new JwtException("Error creating token", e);
    }
  }

  public String generateToken(String subject) {
    return buildToken(subject, new HashMap<>());
  }

  public String generateToken(String subject, Map<String, Object> claims) {
    return buildToken(subject, claims);
  }

  protected JwtClaims verifyTokenAndGetClaims(String jwt) {
    try {
      JwtConsumer jwtConsumer = new JwtConsumerBuilder()
              .setRequireExpirationTime()
              .setAllowedClockSkewInSeconds(30)
              .setRequireSubject()
              .setExpectedIssuer(issuer)
              .setExpectedAudience(audience)
              .setVerificationKey(getPublicKey())
              .build();
      return jwtConsumer.processToClaims(jwt);
    } catch (InvalidJwtException e) {
      throw new JwtException("Error verifying token", e);
    }
  }
}
