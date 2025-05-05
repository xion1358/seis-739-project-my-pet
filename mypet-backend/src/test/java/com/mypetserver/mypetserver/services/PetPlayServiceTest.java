package com.mypetserver.mypetserver.services;

import com.mypetserver.mypetserver.repository.PetRepository;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PetPlayServiceTest {

    @Test
    void testPetAPetSuccess() {
        PetRepository petRepository = mock(PetRepository.class);
        PetManagerService petManagerService = mock(PetManagerService.class);
        PetPlayService petPlayService = new PetPlayService(petRepository, petManagerService);

        boolean result = petPlayService.petAPet(1);

        assertTrue(result);
        verify(petRepository, times(1)).petAPet(1);
        verify(petManagerService, times(1)).updatePetFromRepo(1);
    }

    @Test
    void testPetAPetThrowsException() {
        PetRepository petRepository = mock(PetRepository.class);
        PetManagerService petManagerService = mock(PetManagerService.class);
        PetPlayService petPlayService = new PetPlayService(petRepository, petManagerService);

        doThrow(new RuntimeException("Error")).when(petRepository).petAPet(1);

        assertThrows(RuntimeException.class, () -> petPlayService.petAPet(1));

        verify(petRepository).petAPet(1);
        verify(petManagerService, never()).updatePetFromRepo(anyInt());
    }
}
