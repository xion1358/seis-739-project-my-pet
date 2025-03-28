package com.mypetserver.mypetserver.services;

import com.mypetserver.mypetserver.dto.LoginRequest;
import com.mypetserver.mypetserver.dto.Owner;
import com.mypetserver.mypetserver.repository.OwnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/** Unit tests for {@link AuthenticationService} **/
public class AuthenticationServiceTest {

    private final OwnerRepository ownerRepo = Mockito.mock(OwnerRepository.class);

    private Owner mockOwner;

    @InjectMocks
    private AuthenticationService authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        mockOwner = new Owner();
        mockOwner.setUsername("mockUsername");
        mockOwner.setPassword("mockPassword");
        mockOwner.setDisplayName("mockDisplayName");
        mockOwner.setEmail("mockEmail");
    }

    @Test
    void testAuthenticateSuccess() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);

        LoginRequest mockReq = new LoginRequest("mockUsername", "mockPassword");
        String token = authService.authenticate(mockReq.getUsername(), mockReq.getPassword());

        assertNotNull(token, "Authentication response should not be null");
    }

    @Test
    void testAuthenticateFailed() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        LoginRequest mockReq = new LoginRequest("mockUsername", "mockPassword");

        assertThrows(AuthenticationException.class, () -> {
            authService.authenticate(mockReq.getUsername(), mockReq.getPassword());
        });
    }
}

