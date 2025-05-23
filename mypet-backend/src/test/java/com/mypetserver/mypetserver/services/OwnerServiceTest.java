package com.mypetserver.mypetserver.services;

import com.mypetserver.mypetserver.entities.Owner;
import com.mypetserver.mypetserver.repository.OwnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OwnerServiceTest {

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private OwnerService ownerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveOwner() {
        Owner owner = new Owner(
                "mockOwner",
                "mockDisplayName",
                "mockEmail",
                "password123");

        when(ownerRepository.save(owner)).thenReturn(owner);
        ownerService.saveOwner(owner);

        verify(ownerRepository, times(1)).save(owner);
    }

    @Test
    public void testSaveOwnerWhenSaveFails() {
        Owner owner = new Owner(
                "mockOwner",
                "mockDisplayName",
                "mockEmail",
                "password123");

        when(ownerRepository.findOwnerByUsername("mockOwner")).thenReturn(null);

        doThrow(new RuntimeException("Error")).when(ownerRepository).save(owner);

        try {
            ownerService.saveOwner(owner);
        } catch (RuntimeException e) {
            assertEquals("Error", e.getMessage());
        }

        verify(ownerRepository, times(1)).save(owner);
    }

    @Test
    public void testSaveOwnerWhenOwnerAlreadyExists() {
        Owner existingOwner = new Owner(
                "mockOwner",
                "existingDisplay",
                "existingEmail",
                "password123"
        );

        when(ownerRepository.findOwnerByUsername("mockOwner")).thenReturn(existingOwner);

        Owner newOwner = new Owner(
                "mockOwner",
                "newDisplay",
                "newEmail",
                "password123"
        );

        try {
            ownerService.saveOwner(newOwner);
        } catch (IllegalStateException e) {
            assertEquals("Owner with username 'mockOwner' already exists", e.getMessage());
        }

        verify(ownerRepository, never()).save(any());
    }

    @Test
    public void testOwnerExistsWhenOwnerExists() {
        String username = "existingOwner";
        Owner existingOwner = new Owner(username, "displayName", "email@example.com", "password");

        when(ownerRepository.findOwnerByUsername(username)).thenReturn(existingOwner);

        boolean result = ownerService.ownerExists(username);

        assertTrue(result, "Owner should exist");
    }

    @Test
    public void testOwnerExistsWhenOwnerDoesNotExist() {
        String username = "nonExistingOwner";
        when(ownerRepository.findOwnerByUsername(username)).thenReturn(null);

        boolean result = ownerService.ownerExists(username);

        assertFalse(result, "Owner should not exist");
    }
}
