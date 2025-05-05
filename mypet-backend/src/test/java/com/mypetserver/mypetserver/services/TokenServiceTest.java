package com.mypetserver.mypetserver.services;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TokenServiceTest {

    @Autowired
    private TokenService tokenService;

    @Test
    void testGenerateToken() {
        String token = tokenService.generateToken("mockUser");
        assertNotNull(token);
    }

    @Test
    void testGetJWTTokenReturnsToken() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer abc.def.ghi");

        String token = tokenService.getJWTToken(mockRequest);
        assertEquals("abc.def.ghi", token);
    }

    @Test
    void testGetJWTTokenReturnsNullIfHeaderMissing() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader("Authorization")).thenReturn(null);

        String token = tokenService.getJWTToken(mockRequest);
        assertNull(token);
    }

    @Test
    void testValidateJWTTokenWithValidClaims() {
        String token = "mockToken";

        // Spy and mock parseJWTToken
        TokenService spyService = Mockito.spy(tokenService);
        Claims mockClaims = mock(Claims.class);
        when(mockClaims.getExpiration()).thenReturn(new Date(System.currentTimeMillis() + 10000));

        doReturn(mockClaims).when(spyService).parseJWTToken(token);

        assertTrue(spyService.validateJWTToken(token));
    }

    @Test
    void testValidateJWTTokenWithExpiredClaims() {
        String token = "mockToken";

        TokenService spyService = Mockito.spy(tokenService);
        Claims mockClaims = mock(Claims.class);
        when(mockClaims.getExpiration()).thenReturn(new Date(System.currentTimeMillis() - 10000)); // expired

        doReturn(mockClaims).when(spyService).parseJWTToken(token);

        assertFalse(spyService.validateJWTToken(token));
    }

    @Test
    void testValidateJWTTokenWithNullClaims() {
        String token = "mockToken";

        TokenService spyService = Mockito.spy(tokenService);
        doReturn(null).when(spyService).parseJWTToken(token);

        assertFalse(spyService.validateJWTToken(token));
    }

    @Test
    void testParseUsernameFromJWT() {
        String token = "mockToken";

        TokenService spyService = Mockito.spy(tokenService);
        Claims mockClaims = mock(Claims.class);
        when(mockClaims.getSubject()).thenReturn("mockUser");

        doReturn(mockClaims).when(spyService).parseJWTToken(token);

        assertEquals("mockUser", spyService.parseUsernameFromJWT(token));
    }

    @Test
    void testParseUsernameFromJWTWhenParsingFails() {
        String token = "invalidToken";

        TokenService spyService = Mockito.spy(tokenService);
        doReturn(null).when(spyService).parseJWTToken(token);

        assertNull(spyService.parseUsernameFromJWT(token));
    }

    @Test
    void testValidateParametersMatchesUsername() {
        String token = "mockToken";

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getParameter("owner")).thenReturn("mockUser");

        TokenService spyService = Mockito.spy(tokenService);
        doReturn("mockUser").when(spyService).parseUsernameFromJWT(token);

        assertTrue(spyService.validateParameters(mockRequest, token));
    }

    @Test
    void testValidateParametersMismatch() {
        String token = "mockToken";

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getParameter("owner")).thenReturn("someoneElse");

        TokenService spyService = Mockito.spy(tokenService);
        doReturn("mockUser").when(spyService).parseUsernameFromJWT(token);

        assertFalse(spyService.validateParameters(mockRequest, token));
    }

    @Test
    void testValidateParametersNoOwnerParameter() {
        String token = "mockToken";

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getParameter("owner")).thenReturn(null);

        assertTrue(tokenService.validateParameters(mockRequest, token));
    }
}
