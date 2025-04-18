package com.mypetserver.mypetserver.config;

import com.mypetserver.mypetserver.filters.JwtFilter;
import com.mypetserver.mypetserver.services.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

/**
 * This Class defines configurations for Spring Security
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final UserDetailsService ownerDetailsService;
    private final PasswordEncoder encoder;
    private final CorsConfig corsConfig;
    private final TokenService tokenService;

    @Autowired
    public SecurityConfig(UserDetailsService ownerDetailsService, PasswordEncoder encoder, CorsConfig corsConfig, TokenService tokenService) {
        this.ownerDetailsService = ownerDetailsService;
        this.encoder = encoder;
        this.corsConfig = corsConfig;
        this.tokenService = tokenService;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(ownerDetailsService).passwordEncoder(encoder);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic(httpBasic -> httpBasic.disable());
        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(requests -> {
           requests.requestMatchers("/login").permitAll();
            requests.requestMatchers("/ws-pet/**").permitAll();
            requests.requestMatchers("/registration").permitAll();
           requests.anyRequest().authenticated();
        });

        http.addFilterBefore(this.corsConfig.corsFilter(), CorsFilter.class);
        http.addFilterBefore(new JwtFilter(this.ownerDetailsService, this.tokenService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
