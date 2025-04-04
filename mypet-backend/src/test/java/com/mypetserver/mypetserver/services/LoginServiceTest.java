package com.mypetserver.mypetserver.services;

import com.mypetserver.mypetserver.dto.LoginRequest;
import com.mypetserver.mypetserver.dto.LoginResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class LoginServiceTest {
    @Autowired
    private LoginService loginService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private TokenService tokenService;

    @Test
    public void testLoginSuccess() {
        String username = "testUser";
        String password = "testPassword";
        String expectedToken = "testToken";


        Authentication mockAuthentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);
        when(mockAuthentication.isAuthenticated()).thenReturn(true);
        when(tokenService.generateToken(username)).thenReturn(expectedToken);

        LoginRequest loginRequest = new LoginRequest(username, password);

        ResponseEntity<LoginResponse> response = loginService.login(loginRequest);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedToken, response.getBody().getToken());
    }

    @Test
    public void testLoginFailed() {
        String username = "testUser";
        String password = "badPassword";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Authentication failed"));

        LoginRequest loginRequest = new LoginRequest(username, password);

        try {
            loginService.login(loginRequest);
        } catch (BadCredentialsException e) {
            assertEquals("Authentication failed", e.getMessage());
        }
    }

}
