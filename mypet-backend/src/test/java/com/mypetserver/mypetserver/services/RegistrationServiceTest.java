package com.mypetserver.mypetserver.services;

import com.mypetserver.mypetserver.dto.Owner;
import com.mypetserver.mypetserver.dto.RegistrationRequest;
import com.mypetserver.mypetserver.dto.RegistrationResponse;
import com.mypetserver.mypetserver.repository.OwnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

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
    public void testRegistration() {
        RegistrationRequest registrationRequest =
                new RegistrationRequest(
                        "mockUsername",
                        "mockDisplayName",
                        "mockEmail",
                        "mockPassword");

        Mockito.doNothing().when(ownerService).saveOwner(Mockito.any(Owner.class));

        assertNotNull(registrationService.register(registrationRequest));
    }
}
