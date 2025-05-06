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

    @Test
    void testGetSharedPetsWhenGoingToNextAndHavingNext() {
        Pet pet1 = createTestPet("bob", 101);
        Pet pet2 = createTestPet("bob", 102);
        Pet pet3 = createTestPet("carol", 103);
        Pet pet4 = createTestPet("carol", 104);
        Pet pet5 = createTestPet("carol", 105);
        Pet pet6 = createTestPet("carol", 106);
        Pet pet7 = createTestPet("carol", 107);
        Pet pet8 = createTestPet("carol", 108);

        List<Pet> mockSharedPetsNext = List.of(pet3, pet4, pet5, pet6, pet7);

        List<Pet> mockSharedPetsNextAfter = List.of(pet8);
        List<Pet> mockSharedPetsPreviousAfter = List.of(pet1, pet2);

        when(petRepository.findNextSharedPets(102, 5)).thenReturn(mockSharedPetsNext);

        when(petRepository.findNextSharedPets(107, 5)).thenReturn(mockSharedPetsNextAfter);
        when(petRepository.findPreviousSharedPets(103, 5)).thenReturn(mockSharedPetsPreviousAfter);

        Map<String, Object> result = petManagerService.getSharedPets(102, "next");

        assertTrue(result.containsKey("pets"));
        assertTrue(result.containsKey("hasPrevious"));
        assertTrue(result.containsKey("hasNext"));

        @SuppressWarnings("unchecked")
        List<Pet> resultPets = (List<Pet>) result.get("pets");

        assertEquals(5, resultPets.size());
        assertTrue((Boolean) result.get("hasNext"));
        assertTrue((Boolean) result.get("hasPrevious"));
    }

    @Test
    void testGetSharedPetsWhenGoingToNextAndNotHavingNext() {
        Pet pet1 = createTestPet("bob", 101);
        Pet pet2 = createTestPet("bob", 102);
        Pet pet3 = createTestPet("carol", 103);
        Pet pet4 = createTestPet("carol", 104);
        Pet pet5 = createTestPet("carol", 105);
        Pet pet6 = createTestPet("carol", 106);
        Pet pet7 = createTestPet("carol", 107);
        Pet pet8 = createTestPet("carol", 108);

        List<Pet> mockSharedPetsNext = List.of(pet8);

        List<Pet> mockSharedPetsNextAfter = List.of();
        List<Pet> mockSharedPetsPreviousAfter = List.of(pet3, pet4, pet5, pet6, pet7);

        when(petRepository.findNextSharedPets(107, 5)).thenReturn(mockSharedPetsNext);

        when(petRepository.findNextSharedPets(108, 5)).thenReturn(mockSharedPetsNextAfter);
        when(petRepository.findPreviousSharedPets(108, 5)).thenReturn(mockSharedPetsPreviousAfter);

        Map<String, Object> result = petManagerService.getSharedPets(107, "next");

        assertTrue(result.containsKey("pets"));
        assertTrue(result.containsKey("hasPrevious"));
        assertTrue(result.containsKey("hasNext"));

        @SuppressWarnings("unchecked")
        List<Pet> resultPets = (List<Pet>) result.get("pets");

        assertEquals(1, resultPets.size());
        assertFalse((Boolean) result.get("hasNext"));
        assertTrue((Boolean) result.get("hasPrevious"));
    }

    @Test
    void testGetSharedPetsWhenGoingToPreviousAndThenHavingNothingPrevious() {
        Pet pet1 = createTestPet("bob", 101);
        Pet pet2 = createTestPet("bob", 102);
        Pet pet3 = createTestPet("carol", 103);
        Pet pet4 = createTestPet("carol", 104);
        Pet pet5 = createTestPet("carol", 105);
        Pet pet6 = createTestPet("carol", 106);
        Pet pet7 = createTestPet("carol", 107);
        Pet pet8 = createTestPet("carol", 108);

        List<Pet> mockSharedPetsPrevious = List.of(pet1);

        List<Pet> mockSharedPetsNextAfter = List.of(pet2, pet3, pet4, pet5, pet6);
        List<Pet> mockSharedPetsPreviousAfter = List.of();

        when(petRepository.findPreviousSharedPets(102, 5)).thenReturn(mockSharedPetsPrevious);

        when(petRepository.findNextSharedPets(101, 5)).thenReturn(mockSharedPetsNextAfter);
        when(petRepository.findPreviousSharedPets(101, 5)).thenReturn(mockSharedPetsPreviousAfter);

        Map<String, Object> result = petManagerService.getSharedPets(102, "previous");

        assertTrue(result.containsKey("pets"));
        assertTrue(result.containsKey("hasPrevious"));
        assertTrue(result.containsKey("hasNext"));

        @SuppressWarnings("unchecked")
        List<Pet> resultPets = (List<Pet>) result.get("pets");

        assertEquals(1, resultPets.size());
        assertTrue((Boolean) result.get("hasNext"));
        assertFalse((Boolean) result.get("hasPrevious"));
    }

    @Test
    void testGetSharedPetsWhenGoingToPreviousAndThenHavingPrevious() {
        Pet pet0 = createTestPet("bob", 100);
        Pet pet1 = createTestPet("bob", 101);
        Pet pet2 = createTestPet("bob", 102);
        Pet pet3 = createTestPet("carol", 103);
        Pet pet4 = createTestPet("carol", 104);
        Pet pet5 = createTestPet("carol", 105);
        Pet pet6 = createTestPet("carol", 106);
        Pet pet7 = createTestPet("carol", 107);
        Pet pet8 = createTestPet("carol", 108);

        List<Pet> mockSharedPetsPrevious = List.of(pet1);

        List<Pet> mockSharedPetsNextAfter = List.of(pet2, pet3, pet4, pet5, pet6);
        List<Pet> mockSharedPetsPreviousAfter = List.of(pet0);

        when(petRepository.findPreviousSharedPets(102, 5)).thenReturn(mockSharedPetsPrevious);

        when(petRepository.findNextSharedPets(101, 5)).thenReturn(mockSharedPetsNextAfter);
        when(petRepository.findPreviousSharedPets(101, 5)).thenReturn(mockSharedPetsPreviousAfter);

        Map<String, Object> result = petManagerService.getSharedPets(102, "previous");

        assertTrue(result.containsKey("pets"));
        assertTrue(result.containsKey("hasPrevious"));
        assertTrue(result.containsKey("hasNext"));

        @SuppressWarnings("unchecked")
        List<Pet> resultPets = (List<Pet>) result.get("pets");

        assertEquals(1, resultPets.size());
        assertTrue((Boolean) result.get("hasNext"));
        assertTrue((Boolean) result.get("hasPrevious"));
    }

    @Test
    void testGetSharedPetsWhenTryingNextAndHavingNoNext() {
        Pet pet1 = createTestPet("bob", 101);
        Pet pet2 = createTestPet("carol", 102);
        List<Pet> mockSharedPets = List.of(pet1, pet2);

        when(petRepository.findNextSharedPets(103, 5)).thenReturn(List.of());
        when(petRepository.findPreviousSharedPets(101, 5)).thenReturn(mockSharedPets);

        Map<String, Object> result = petManagerService.getSharedPets(103, "next");

        assertTrue(result.containsKey("pets"));
        assertTrue(result.containsKey("hasPrevious"));
        assertTrue(result.containsKey("hasNext"));

        @SuppressWarnings("unchecked")
        List<Pet> resultPets = (List<Pet>) result.get("pets");

        assertEquals(0, resultPets.size());
        assertFalse((Boolean) result.get("hasNext"));
        assertFalse((Boolean) result.get("hasPrevious"));
    }

    @Test
    void testGetSharedPetsWhenTryingPreviousAndHavingNoPrevious() {
        Pet pet1 = createTestPet("bob", 104);
        Pet pet2 = createTestPet("carol", 105);
        List<Pet> mockSharedPets = List.of(pet1, pet2);

        when(petRepository.findNextSharedPets(103, 5)).thenReturn(mockSharedPets);
        when(petRepository.findPreviousSharedPets(103, 5)).thenReturn(List.of());

        Map<String, Object> result = petManagerService.getSharedPets(103, "Previous");

        assertTrue(result.containsKey("pets"));
        assertTrue(result.containsKey("hasPrevious"));
        assertTrue(result.containsKey("hasNext"));

        @SuppressWarnings("unchecked")
        List<Pet> resultPets = (List<Pet>) result.get("pets");

        assertEquals(0, resultPets.size());
        assertFalse((Boolean) result.get("hasNext"));
        assertFalse((Boolean) result.get("hasPrevious"));
    }

}
