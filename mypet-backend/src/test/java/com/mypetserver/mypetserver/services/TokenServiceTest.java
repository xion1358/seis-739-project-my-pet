package com.mypetserver.mypetserver.services;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TokenServiceTest {
    private final static Logger logger = LoggerFactory.getLogger(TokenServiceTest.class);

    @Autowired
    private TokenService tokenService;

    @Test
    void testGenerateToken() {
        try (MockedStatic<Jwts> mockedJwts = Mockito.mockStatic(Jwts.class)) {
            mockedJwts.when(Jwts::builder).thenReturn("mockToken");
            assertNotNull(this.tokenService.generateToken("mockUsername"));
        } catch (Exception e) {
            logger.error("Test exception: {}", e.getMessage());
        }
    }
}
