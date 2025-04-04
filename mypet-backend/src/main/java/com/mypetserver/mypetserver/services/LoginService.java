package com.mypetserver.mypetserver.services;

import com.mypetserver.mypetserver.dto.LoginRequest;
import com.mypetserver.mypetserver.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Autowired
    public LoginService(AuthenticationManager authenticationManager,
                        TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
        String token = authenticate(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(
                new LoginResponse(loginRequest.getUsername(), token));
    }

    private String authenticate(String username, String password) throws AuthenticationException {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        if (authentication.isAuthenticated()) {
            return this.tokenService.generateToken(username);
        }

        throw new BadCredentialsException("Authentication failed");
    }
}
