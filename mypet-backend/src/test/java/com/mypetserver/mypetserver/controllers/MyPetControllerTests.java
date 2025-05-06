package com.mypetserver.mypetserver.controllers;

import com.mypetserver.mypetserver.dto.LoginRequest;
import com.mypetserver.mypetserver.dto.LoginResponse;
import com.mypetserver.mypetserver.dto.RegistrationRequest;
import com.mypetserver.mypetserver.dto.RegistrationResponse;
import com.mypetserver.mypetserver.entities.Pet;
import com.mypetserver.mypetserver.services.LoginService;
import com.mypetserver.mypetserver.services.PetManagerService;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MyPetControllerTests {

    @MockitoBean
    private PetManagerService petManagerService;

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

        RegistrationResponse mockRes = new RegistrationResponse("testuser", "token", "Registered Successfully");
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
    void testRegistrationFailedDueToMissingUsername() throws Exception {
        String credentials = "{\"username\":\"\",\"displayName\":\"testdisplayname\", \"email\":\"testemail@email.com\", \"password\":\"password123\"}";

        mockMvc.perform(post("/registration")
                        .characterEncoding("UTF-8")
                        .contentType("application/json")
                        .content(credentials))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegistrationFailedDueToMissingEmail() throws Exception {
        String credentials = "{\"username\":\"testuser\",\"displayName\":\"testdisplayname\", \"email\":\"\", \"password\":\"password123\"}";

        mockMvc.perform(post("/registration")
                        .characterEncoding("UTF-8")
                        .contentType("application/json")
                        .content(credentials))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegistrationFailedDueToMissingDisplayName() throws Exception {
        String credentials = "{\"username\":\"testuser\",\"displayName\":\"\", \"email\":\"testemail@email.com\", \"password\":\"password123\"}";

        mockMvc.perform(post("/registration")
                        .characterEncoding("UTF-8")
                        .contentType("application/json")
                        .content(credentials))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegistrationFailedDueToMissingPassword() throws Exception {
        String credentials = "{\"username\":\"testuser\",\"displayName\":\"testdisplayname\", \"email\":\"testemail@email.com\", \"password\":\"\"}";

        mockMvc.perform(post("/registration")
                        .characterEncoding("UTF-8")
                        .contentType("application/json")
                        .content(credentials))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testValidateLoginSuccess() throws Exception {
        String token = "validToken";
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user", null, List.of(new SimpleGrantedAuthority("USER"))));

        mockMvc.perform(post("/validate")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testLoginValidationFailsDueToPassword() throws Exception {
        String credentials = "{\"username\":\"testuser\", \"password\":\"\"}";

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credentials))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLoginValidationFailsDueToUsername() throws Exception {
        String credentials = "{\"username\":\"\", \"password\":\"Password123\"}";

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credentials))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetPets() throws Exception {
        String token = "validToken";
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user", null, List.of(new SimpleGrantedAuthority("USER"))));

        ArrayList<Pet> mockedPets = new ArrayList<>();
        mockedPets.add(new Pet());
        mockedPets.add(new Pet());
        when(petManagerService.getPets("testowner")).thenReturn(mockedPets);

        mockMvc.perform(get("/get-pets")
                        .header("Authorization", "Bearer " + token)
                        .param("owner", "testowner")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }


    @Test
    void testRegisterPetForViewingSuccess() throws Exception {
        String ownerName = "testowner";
        int petId = 1;
        String token = "validToken";
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user", null, List.of(new SimpleGrantedAuthority("USER"))));

        Pet mockedPet = new Pet();
        mockedPet.setPetId(petId);
        when(petManagerService.registerPet(ownerName, petId)).thenReturn(mockedPet);

        mockMvc.perform(post("/register-pet-for-viewing")
                        .header("Authorization", "Bearer " + token)
                        .param("owner", ownerName)
                        .param("id", String.valueOf(petId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.petId").value(petId));
    }


    @Test
    void testRegisterPetForViewingFailed() throws Exception {
        String ownerName = "testowner";
        int petId = 1;
        String token = "validToken";
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user", null, List.of(new SimpleGrantedAuthority("USER"))));

        when(petManagerService.registerPet(ownerName, petId)).thenReturn(null);

        mockMvc.perform(post("/register-pet-for-viewing")
                        .header("Authorization", "Bearer " + token)
                        .param("owner", ownerName)
                        .param("id", String.valueOf(petId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetSharedPets() throws Exception {
        int cursor = 10;
        String direction = "next";
        String token = "validToken";

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user", null, List.of(new SimpleGrantedAuthority("USER"))));

        Map<String, Object> mockResult = new HashMap<>();
        List<Pet> mockPets = List.of(new Pet(), new Pet());
        mockResult.put("pets", mockPets);
        mockResult.put("hasPrevious", true);
        mockResult.put("hasNext", false);

        when(petManagerService.getSharedPets(cursor, direction)).thenReturn(mockResult);

        mockMvc.perform(get("/get-shared-pets")
                        .header("Authorization", "Bearer " + token)
                        .param("cursor", String.valueOf(cursor))
                        .param("direction", direction)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pets.length()").value(2))
                .andExpect(jsonPath("$.hasPrevious").value(true))
                .andExpect(jsonPath("$.hasNext").value(false));
    }

    @Test
    void testGetSharedPetsMissingCursor() throws Exception {
        String token = "validToken";
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user", null, List.of(new SimpleGrantedAuthority("USER"))));

        mockMvc.perform(get("/get-shared-pets")
                        .header("Authorization", "Bearer " + token)
                        .param("direction", "next")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Missing 'cursor' or 'direction' parameter"));
    }

    @Test
    void testGetSharedPetsInvalidCursor() throws Exception {
        String token = "validToken";
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user", null, List.of(new SimpleGrantedAuthority("USER"))));

        mockMvc.perform(get("/get-shared-pets")
                        .header("Authorization", "Bearer " + token)
                        .param("cursor", "notAnInteger")
                        .param("direction", "next")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("'cursor' must be an integer"));
    }

    @Test
    void testGetSharedPetsInvalidDirection() throws Exception {
        String token = "validToken";
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user", null, List.of(new SimpleGrantedAuthority("USER"))));

        mockMvc.perform(get("/get-shared-pets")
                        .header("Authorization", "Bearer " + token)
                        .param("cursor", "10")
                        .param("direction", "upward")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("'direction' must be 'next' or 'previous'"));
    }


}
