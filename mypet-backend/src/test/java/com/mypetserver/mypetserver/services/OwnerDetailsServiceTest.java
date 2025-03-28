package com.mypetserver.mypetserver.services;

import com.mypetserver.mypetserver.dto.Owner;
import com.mypetserver.mypetserver.dto.User;
import com.mypetserver.mypetserver.repository.OwnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class OwnerDetailsServiceTest {
    private static final String username = "testuser";
    private UserDetails userDetails;
    private Owner owner;

    @InjectMocks
    private OwnerDetailsService ownerDetailsService;

    @Mock
    private OwnerRepository ownerRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        userDetails = null;
        owner = new Owner();
    }

    @Test
    void testLoadUserByUsernameSuccess() {
        when(ownerRepository.getOwnerByUsername(username)).thenReturn(owner);

        this.userDetails = this.ownerDetailsService.loadUserByUsername(username);
        assertNotNull(userDetails);
    }

    @Test
    void testLoadUserByUsernameFailed() {
        when(ownerRepository.getOwnerByUsername(username)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> this.ownerDetailsService.loadUserByUsername(username));
    }
}
