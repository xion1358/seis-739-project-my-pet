package com.mypetserver.mypetserver.services;

import com.mypetserver.mypetserver.entities.Pet;
import com.mypetserver.mypetserver.entities.PetTypes;
import com.mypetserver.mypetserver.models.PetActions;
import com.mypetserver.mypetserver.repository.FoodRepository;
import com.mypetserver.mypetserver.repository.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PetManagerServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private PetRepository petRepository;

    @Mock
    private FoodRepository foodRepository;

    @Mock
    private PetFoodService petFoodService;

    @Mock
    private PetBehaviorService petBehaviorService;

    @InjectMocks
    private PetManagerService petManagerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Pet createTestPet(String owner, int petId) {
        Pet pet = new Pet("TestPet", owner, PetTypes.getPetTypeByName("cat"), 50, 50, 300, 300, "right", PetActions.IDLE.getValue(), 0);
        pet.setPetId(petId);
        return pet;
    }

    @Test
    void testRegisterPetWithValidOwnerAndPet() {
        Pet pet = createTestPet("alice", 1);
        when(petRepository.findByPetId(1)).thenReturn(pet);

        Pet result = petManagerService.registerPet("alice", 1);

        assertNotNull(result);
        verify(petRepository, times(2)).findByPetId(1);
    }

    @Test
    void testRegisterPetWithInvalidOwner() {
        Pet pet = createTestPet("bob", 2);
        when(petRepository.findByPetId(2)).thenReturn(pet);

        Pet result = petManagerService.registerPet("alice", 2);

        assertNull(result);
    }

    @Test
    void testUnregisterPet() {
        Pet pet = createTestPet("alice", 1);
        when(petRepository.findByPetId(1)).thenReturn(pet);

        petManagerService.registerPet("alice", 1);
        petManagerService.unregisterPet(1);

        Pet result = petManagerService.registerPet("alice", 1);
        assertNotNull(result);
    }

    @Test
    void testGenerateANewPetForOwnerLessThan3Pets() {
        when(petRepository.getPetsByPetOwner("alice")).thenReturn(Collections.emptyList());

        boolean result = petManagerService.generateANewPetForOwner("alice", "Fluffy", "Cat");

        assertTrue(result);
        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    void testGenerateANewPetForOwnerAlready3Pets() {
        when(petRepository.getPetsByPetOwner("bob")).thenReturn(Arrays.asList(new Pet(), new Pet(), new Pet()));

        boolean result = petManagerService.generateANewPetForOwner("bob", "Fluffy", "Cat");

        assertFalse(result);
    }

    @Test
    void testAbandonPetValid() {
        Pet pet = createTestPet("alice", 1);
        when(petRepository.findByPetId(1)).thenReturn(pet);

        boolean result = petManagerService.abandonPet("alice", 1);

        assertTrue(result);
        verify(petRepository, times(1)).removePetByPetId(1);
    }

    @Test
    void testAbandonPetFailsWhenPetDoesNotExist() {
        when(petRepository.findByPetId(1)).thenReturn(null);

        Boolean result = petManagerService.abandonPet("alice", 1);

        assertFalse(result);
        verify(petRepository, times(1)).findByPetId(1);
        verify(petRepository, never()).removePetByPetId(anyInt());
    }

    @Test
    void testAbandonPetFailsWhenOwnerMismatch() {
        Pet pet = createTestPet("bob", 1);
        when(petRepository.findByPetId(1)).thenReturn(pet);

        Boolean result = petManagerService.abandonPet("alice", 1);

        assertFalse(result);
        verify(petRepository, times(1)).findByPetId(1);
        verify(petRepository, never()).removePetByPetId(anyInt());
    }

    @Test
    void testAbandonPetFailsWhenPetIsShared() {
        Pet pet = createTestPet("alice", 1);
        pet.setShared(1);
        when(petRepository.findByPetId(1)).thenReturn(pet);

        Boolean result = petManagerService.abandonPet("alice", 1);

        assertFalse(result);
        verify(petRepository, times(1)).findByPetId(1);
        verify(petRepository, never()).removePetByPetId(anyInt());
    }

    @Test
    void testSharePet() {
        Pet pet = createTestPet("alice", 1);
        when(petRepository.findByPetId(1)).thenReturn(pet);

        boolean result = petManagerService.sharePet("alice", 1);

        assertTrue(result);
        verify(petRepository).save(pet);
    }

    @Test
    void testSharePetFailsWhenPetDoesNotExist() {
        when(petRepository.findByPetId(1)).thenReturn(null);

        boolean result = petManagerService.sharePet("alice", 1);

        assertFalse(result);
        verify(petRepository, never()).save(any());
    }

    @Test
    void testSharePetFailsWhenOwnerMismatch() {
        Pet pet = createTestPet("bob", 1);
        when(petRepository.findByPetId(1)).thenReturn(pet);

        boolean result = petManagerService.sharePet("alice", 1);

        assertFalse(result);
        verify(petRepository, never()).save(any());
    }

    @Test
    void testUnsharePet() {
        Pet pet = createTestPet("alice", 1);
        when(petRepository.findByPetId(1)).thenReturn(pet);

        boolean result = petManagerService.unsharePet("alice", 1);

        assertTrue(result);
        verify(petRepository).save(pet);
        verify(messagingTemplate).convertAndSend("/topic/shared/pet/1", "CLOSE");
    }

    @Test
    void testUnsharePetFailsWhenPetDoesNotExist() {
        when(petRepository.findByPetId(1)).thenReturn(null);

        boolean result = petManagerService.unsharePet("alice", 1);

        assertFalse(result);
        verify(petRepository, never()).save(any());
        verify(messagingTemplate, never()).convertAndSend(anyString(), Optional.ofNullable(any()));
    }

    @Test
    void testUnsharePetFailsWhenOwnerMismatch() {
        Pet pet = createTestPet("bob", 1);
        when(petRepository.findByPetId(1)).thenReturn(pet);

        boolean result = petManagerService.unsharePet("alice", 1);

        assertFalse(result);
        verify(petRepository, never()).save(any());
        verify(messagingTemplate, never()).convertAndSend(anyString(), Optional.ofNullable(any()));
    }

    @Test
    void testAddSubscriber() {
        petManagerService.addSubscriber(1, "session123");

        petManagerService.removeSubscriberBySessionId("session123");

        verify(petRepository).save(any());
    }
}
