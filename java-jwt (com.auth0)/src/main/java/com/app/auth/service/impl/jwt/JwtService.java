package com.app.auth.service.impl.jwt;

import com.app.auth.util.exception.InternalErrorException;
import com.app.auth.util.exception.JwtException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
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
import java.util.UUID;

@Service
public class JwtService {

  private final String issuer = System.getenv("JWT_ISSUER");

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

  private Algorithm getAlgorithm() {
    return Algorithm.RSA256(getPublicKey(), getPrivateKey());
  }

  private String buildToken(String subject, Map<String, Object> claims) {
    try {
      long expiresAt = 1000 * 60 * 60 * 24L;
      return JWT.create()
              .withIssuer(issuer)
              .withSubject(subject)
              .withPayload(claims)
              .withIssuedAt(new Date(System.currentTimeMillis()))
              .withExpiresAt(new Date(System.currentTimeMillis() + expiresAt))
              .withJWTId(UUID.randomUUID().toString())
              .sign(getAlgorithm());
    } catch (JWTCreationException e) {
      throw new JwtException("Error creating token", e);
    }
  }

  public String generateToken(String subject) {
    return buildToken(subject, new HashMap<>());
  }

  public String generateToken(String subject, Map<String, Object> claims) {
    return buildToken(subject, claims);
  }

  protected String verifyTokenAndGetSubject(String token) {
    DecodedJWT decodedJWT;
    try {
      JWTVerifier verifier = JWT.require(getAlgorithm())
              .withIssuer(issuer)
              .build();
      decodedJWT = verifier.verify(token);
      return decodedJWT.getSubject();
    } catch (JWTVerificationException e) {
      throw new JwtException("Error verifying token", e);
    }
  }
}
