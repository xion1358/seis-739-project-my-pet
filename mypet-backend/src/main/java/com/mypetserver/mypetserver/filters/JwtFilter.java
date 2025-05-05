package com.mypetserver.mypetserver.filters;

import com.mypetserver.mypetserver.services.TokenService;
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

import java.io.IOException;

/**
 * This class defines the filter to be used for authentication of a given token
 * Note: It currently utilizes an environment defined SECRET to verify the token.
 * This is to be migrated to a key vault at some point in the future for better security.
 */
public class JwtFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    private final UserDetailsService ownerDetailsService;
    private final TokenService tokenService;

    public JwtFilter(UserDetailsService ownerDetailsService, TokenService tokenService) {
        this.ownerDetailsService = ownerDetailsService;
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = this.tokenService.getJWTToken(request);
        // logger.info("JWT Token: " + token);

        if (token != null && this.tokenService.validateJWTToken(token) && this.tokenService.validateParameters(request, token)) {
            String username = this.tokenService.parseJWTToken(token).getSubject();

            UserDetails userDetails = this.ownerDetailsService.loadUserByUsername(username);

            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
}
