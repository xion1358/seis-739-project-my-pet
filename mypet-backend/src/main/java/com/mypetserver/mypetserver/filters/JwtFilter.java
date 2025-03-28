package com.mypetserver.mypetserver.filters;

import com.mypetserver.mypetserver.services.OwnerDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;

/**
 * This class defines the filter to be used for authentication of a given token
 * Note: It currently utilizes an environment defined SECRET to verify the token.
 * This is to be migrated to a key vault at some point in the future for better security.
 */
public class JwtFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    private static final String SECRET = System.getenv("SECRET"); // TODO: Migrate secret to cloud key vault
    private final SecretKey secretKey = Keys.hmacShaKeyFor((SECRET.getBytes()));
    private final UserDetailsService ownerDetailsService;

    public JwtFilter(UserDetailsService ownerDetailsService) {
        this.ownerDetailsService = ownerDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getJWTToken(request);

        if (token != null && validateJWTToken(token)) {
            String username = this.parseJWTToken(token).getSubject();

            UserDetails userDetails = this.ownerDetailsService.loadUserByUsername(username);

            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private String getJWTToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private boolean validateJWTToken(String token) {
        return this.parseJWTToken(token) != null;
    }

    private Claims parseJWTToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(this.secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            logger.error("Failed to parse token: {}", e.getMessage());
            return null;
        }
    }
}
