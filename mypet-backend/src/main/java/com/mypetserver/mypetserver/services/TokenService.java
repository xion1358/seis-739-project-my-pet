package com.mypetserver.mypetserver.services;

import com.mypetserver.mypetserver.filters.JwtFilter;
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
                // Sets expiration to 10 mins TODO: update to a value that makes more sense for production
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                .signWith(this.secretKey)
                .compact();
    }
}
