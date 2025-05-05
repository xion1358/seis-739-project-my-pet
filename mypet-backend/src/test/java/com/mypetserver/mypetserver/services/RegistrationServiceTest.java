package com.mypetserver.mypetserver.services;

import com.mypetserver.mypetserver.dto.RegistrationResponse;
import com.mypetserver.mypetserver.entities.Owner;
import com.mypetserver.mypetserver.dto.RegistrationRequest;
import com.mypetserver.mypetserver.repository.OwnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RegistrationServiceTest {

    @MockitoBean
    private OwnerRepository ownerRepository;

    @MockitoBean
    private OwnerService ownerService;

    @Autowired
    private RegistrationService registrationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegistrationSuccess() {
        RegistrationRequest request = new RegistrationRequest(
                "newUser",
                "New Display Name",
                "newuser@example.com",
                "securePassword123"
        );

        Mockito.when(ownerRepository.getOwnerByUsername("newUser")).thenReturn(null);
        Mockito.when(ownerRepository.getOwnerByEmail("newuser@example.com")).thenReturn(null);

        Mockito.doNothing().when(ownerService).saveOwner(Mockito.any(Owner.class));

        ResponseEntity<RegistrationResponse> response = registrationService.register(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        RegistrationResponse responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("newUser", responseBody.getUsername());
        assertNotNull(responseBody.getToken()); // Should generate a token
        assertEquals("Registered Successfully", responseBody.getMessage());
    }


    @Test
    public void testRegistrationFailsWhenUsernameTaken() {
        RegistrationRequest request = new RegistrationRequest(
                "existingUser",
                "Some Display Name",
                "newEmail@example.com",
                "password123"
        );

        Mockito.when(ownerRepository.getOwnerByUsername("existingUser"))
                .thenReturn(new Owner());

        ResponseEntity<RegistrationResponse> response = registrationService.register(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("existingUser", response.getBody().getUsername());
        assertEquals("Username or Email already taken. Please try again.", response.getBody().getMessage());
        assertNull(response.getBody().getToken());
    }

    @Test
    public void testRegistrationFailsWhenEmailTaken() {
        RegistrationRequest request = new RegistrationRequest(
                "existingUser",
                "Some Display Name",
                "newEmail@example.com",
                "password123"
        );

        Mockito.when(ownerRepository.getOwnerByEmail("newEmail@example.com"))
                .thenReturn(new Owner());

        ResponseEntity<RegistrationResponse> response = registrationService.register(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("existingUser", response.getBody().getUsername());
        assertEquals("Username or Email already taken. Please try again.", response.getBody().getMessage());
        assertNull(response.getBody().getToken());
    }

}
