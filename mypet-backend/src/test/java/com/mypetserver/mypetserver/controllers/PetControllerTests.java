package com.mypetserver.mypetserver.controllers;

import com.mypetserver.mypetserver.dto.LoginRequest;
import com.mypetserver.mypetserver.dto.LoginResponse;
import com.mypetserver.mypetserver.services.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PetControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PetController petController;

    @MockitoBean
    private AuthenticationService authService;

    @BeforeEach
    public void setup() {
    }

    @Test
    void testLoginSuccess() throws Exception {
        String credentials = "{\"username\":\"testuser\",\"password\":\"password123\"}";

        LoginRequest mockReq = new LoginRequest("testuser", "password123");

        LoginResponse mockRes = new LoginResponse("testuser", "token");
        ResponseEntity<LoginResponse> mockResEntity = ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_ENCODING, "UTF-8")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(mockRes);

        when(authService.authenticate(mockReq.getUsername(), mockReq.getPassword())).thenReturn(mockRes.getToken());

        mockMvc.perform(post("/login")
                        .characterEncoding("UTF-8")
                        .contentType("application/json")
                        .content(credentials))
                .andExpect(status().isOk())  // Expect OK status (200)
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.token").value("token"));
    }

    @Test
    void testLoginFailed() throws Exception {
        String credentials = "{\"username\":\"testuser\",\"password\":\"password123\"}";

        LoginRequest mockReq = new LoginRequest("testuser", "password123");

        when(authService.authenticate(mockReq.getUsername(), mockReq.getPassword()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/login")
                        .characterEncoding("UTF-8")
                        .contentType("application/json")
                        .content(credentials))
                .andExpect(status().is(UNAUTHORIZED.value()))
                .andExpect(content().contentType("application/json"));
    }
}
