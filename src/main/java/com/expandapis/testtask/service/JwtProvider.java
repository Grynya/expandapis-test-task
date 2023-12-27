package com.expandapis.testtask.service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);
    private final SecretKey key;
    private final int jwtExpirationMs;
    public JwtProvider(@Value("${jwt.secret}") String secretKey,
                       @Value("${jwt.expiration.ms}")int jwtExpirationMs) {
        this.jwtExpirationMs = jwtExpirationMs;
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        this.key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA512");
    }

    public String generateToken(Authentication authentication) {
        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(authentication.getName())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    public boolean validateToken(String jwt) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(jwt);
            return true;
        } catch (JwtException e) {
            logger.error("JWT validation error", e);
        }
        return false;
    }

    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
