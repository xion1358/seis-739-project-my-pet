package com.mypetserver.mypetserver.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * This class defines the token generation
 * Note: It currently utilizes a given environment variable SECRET.
 * This is to be migrated to a key vault at some point in the future.
 */
@Service
public class TokenService {
    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    private static final String SECRET = System.getenv("SECRET"); // TODO: Migrate secret to cloud key vault
    private final SecretKey secretKey = Keys.hmacShaKeyFor((SECRET.getBytes()));

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                // Sets expiration to 5 hrs TODO: update to a value that makes more sense for production
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 5))
                .signWith(this.secretKey)
                .compact();
    }

    public String getJWTToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    public boolean validateJWTToken(String token) {
        Claims claims = this.parseJWTToken(token);
        return claims != null && !claims.getExpiration().before(new Date());
    }

    public Claims parseJWTToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(this.secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            logger.info("Failed to parse token: {}", e.getMessage());
            return null;
        }
    }

    public String parseUsernameFromJWT(String token) {
        try {
            Claims claims = parseJWTToken(token);
            return claims != null ? claims.getSubject() : null;
        } catch (Exception e) {
            logger.error("Failed to parse username: {}", e.getMessage());
            return null;
        }
    }

    // Currently checks just the owner name. Makes sure the owner name is not spoofed.
    public boolean validateParameters(HttpServletRequest request, String token) {
        String ownerName = request.getParameter("owner");
        if (ownerName == null) {
            return true;
        }
        return ownerName.equals(this.parseUsernameFromJWT(token));
    }

}
