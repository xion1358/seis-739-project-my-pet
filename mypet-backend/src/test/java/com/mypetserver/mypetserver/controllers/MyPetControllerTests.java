package com.mypetserver.mypetserver.controllers;

import com.mypetserver.mypetserver.dto.LoginRequest;
import com.mypetserver.mypetserver.dto.LoginResponse;
import com.mypetserver.mypetserver.dto.RegistrationRequest;
import com.mypetserver.mypetserver.dto.RegistrationResponse;
import com.mypetserver.mypetserver.services.LoginService;
import com.mypetserver.mypetserver.services.RegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MyPetControllerTests {

    @MockitoBean
    private LoginService loginService;

    @MockitoBean
    private RegistrationService registrationService;

    @Autowired
    private PetController petController;

    @Autowired
    private MockMvc mockMvc;

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

        when(loginService.login(mockReq)).thenReturn(mockResEntity);

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

        when(loginService.login(mockReq)).thenThrow(new RuntimeException("Authentication Exception"));

        mockMvc.perform(post("/login")
                        .characterEncoding("UTF-8")
                        .contentType("application/json")
                        .content(credentials))
                .andExpect(status().is(UNAUTHORIZED.value()))
                .andExpect(content().contentType("application/json"));
    }

    @Test
    void testRegistrationSuccess() throws Exception {
        String credentials = "{\"username\":\"testuser\",\"displayName\":\"testdisplayname\", \"email\":\"testemail@email.com\", \"password\":\"password123\"}";

        RegistrationRequest mockReq = new RegistrationRequest("testuser", "testdisplayname", "testemail@email.com", "password123");

        RegistrationResponse mockRes = new RegistrationResponse("testuser", "token");
        ResponseEntity<RegistrationResponse> mockResEntity = ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_ENCODING, "UTF-8")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(mockRes);

        when(registrationService.register(mockReq)).thenReturn(mockResEntity);

        mockMvc.perform(post("/registration")
                        .characterEncoding("UTF-8")
                        .contentType("application/json")
                        .content(credentials))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.token").value("token"));
    }

    @Test
    void testValidateLoginSuccess() throws Exception {
        String token = "token";
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user", null, List.of(new SimpleGrantedAuthority("USER"))));

        mockMvc.perform(post("/validate")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
