package com.mypetserver.mypetserver.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

/**
 * This class defines the authentication service used to authenticate a given user.
 *
 * Note: It currently utilizes a given environment variable SECRET.
 * This is to be migrated to a key vault at some point in the future.
 */
@Service
public class AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private static final String SECRET = System.getenv("SECRET"); // TODO: Migrate secret to cloud key vault
    private final Key key = Keys.hmacShaKeyFor((SECRET.getBytes()));
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationService(AuthenticationManager authenticationManager) throws AuthenticationException {
        this.authenticationManager = authenticationManager;
    }

    public String authenticate(String username, String password) throws AuthenticationException {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        if (authentication.isAuthenticated()) {
            return generateJWTToken(username);
        }

        throw new BadCredentialsException("Authentication failed");
    }

    private String generateJWTToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                // Sets expiration to 10 mins TODO: update to a value that makes more sense for production
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                .signWith(this.key)
                .compact();
    }
}
