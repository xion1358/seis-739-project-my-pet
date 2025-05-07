package com.mypetserver.mypetserver.controllers;

import com.mypetserver.mypetserver.dto.*;
import com.mypetserver.mypetserver.entities.Pet;
import com.mypetserver.mypetserver.entities.PetTypes;
import com.mypetserver.mypetserver.services.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This Class defines RESTful APIs for requests to the server.
 */
@RestController
public class PetController {
    private static final Logger logger = LoggerFactory.getLogger(PetController.class);

    private final LoginService loginService;
    private final RegistrationService registrationService;
    private final PetManagerService petManagerService;
    private final PetFoodService petFoodService;
    private final PetPlayService petPlayService;

    @Autowired
    public PetController(
            LoginService loginService,
            RegistrationService registrationService,
            PetManagerService petManagerService,
            PetFoodService petFoodService,
            PetPlayService petPlayService) {
        this.loginService = loginService;
        this.registrationService = registrationService;
        this.petManagerService = petManagerService;
        this.petFoodService = petFoodService;
        this.petPlayService = petPlayService;
    }

    /**
     * Attempts to authenticate the user's given request against stored credentials.
     * @param loginRequest DTO detailing the login request object. Contains username and password.
     * @return LoginResponse DTO detailing login response object. Contains username and token.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {

        try {
            return this.loginService.login(loginRequest);
        } catch (Exception e) {
            logger.error("Error: could not login user {} - {}", loginRequest.getUsername(),e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).contentType(MediaType.APPLICATION_JSON).build();
        }

    }

    /**
     * Attempts to register the user's given credentials.
     * @param registrationRequest DTO detailing registration request object. Contains username, displayName,
     *                            email, and password.
     *
     * @return RegistrationResponse DTO detailing the registration response object. Contains username, token, and
     * a helpful message (on success or failure).
     */
    @PostMapping("/registration")
    public ResponseEntity<RegistrationResponse> register(@Valid @RequestBody RegistrationRequest registrationRequest) {
        try {
            return this.registrationService.register(registrationRequest);

        } catch (Exception e) {
            logger.error("Error: could not register user {} - {}", registrationRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(null);
        }
    }

    /**
     * Endpoint for validation requests. Send requests here to limit the user's access. Only returns success because
     * sprint boot security filter checks the token before getting here. If you arrive here, you have been validated.
     * @param request http request containing the token in the authorization header.
     * @return http response 200.
     */
    @PostMapping("/validate")
    public ResponseEntity<Void> validate(HttpServletRequest request) {
        return ResponseEntity.ok().build();
    }

    /**
     * Attempts to get the given owners pets.
     * @param request http request containing the owner's username.
     * @return list of pets belonging to the given owner.
     */
    @GetMapping("/get-pets")
    public ResponseEntity<List<Pet>> getPets(HttpServletRequest request) {
        ArrayList<Pet> pets = this.petManagerService.getPets(request.getParameter("owner"));
        return ResponseEntity.ok(pets);
    }

    /**
     * Attempts to register a given pet id for viewing. Pets should be registered for viewing so that they are put
     * in the game loop for evaluation of behaviors and generation of actions.
     * @param request http request containing the owner's username, and pet id number.
     * @return pet that was successfully registered, or an unauthorized status.
     */
    @PostMapping("/register-pet-for-viewing")
    public ResponseEntity<Pet> registerPetForViewing(HttpServletRequest request) {
        String ownerName = request.getParameter("owner");
        Pet registeredPet = this.petManagerService.registerPet(ownerName, Integer.parseInt(request.getParameter("id")));

        // logger.info("Registering pet {} for {}", registeredPet.getPetId(), ownerName);

        return (registeredPet != null) ? ResponseEntity.ok(registeredPet) : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * Attempts to create food for a given pet.
     * @param request http request containing pet id to create for and the type of food to create.
     * @return boolean true if food created, else false.
     */
    @PostMapping("/create-pet-food")
    public ResponseEntity<Boolean> createPetFood(HttpServletRequest request) {
        logger.info("Creating pet food for {}", request.getParameter("id"));
        return ResponseEntity.ok(this.petFoodService.createPetFood(Integer.parseInt(request.getParameter("id")), request.getParameter("food")));
    }

    /**
     * Attempts to pet a pet (to raise affection).
     * @param request http request containing pet id.
     * @return boolean true if pet was successful, else false.
     */
    @PostMapping("/pet-a-pet")
    public ResponseEntity<Boolean> petAPet(HttpServletRequest request) {
        logger.info("Petting pet {}", request.getParameter("id"));
        return ResponseEntity.ok(this.petPlayService.petAPet(Integer.parseInt(request.getParameter("id"))));
    }

    /**
     * Attempts to get the pet types defined by the server.
     * @param request http request. No body element necessary.
     * @return the types of pets defined by the server.
     */
    @PostMapping("/pet-types")
    public ResponseEntity<List<PetTypes>> getPetTypes(HttpServletRequest request) {
        return ResponseEntity.ok(this.petManagerService.getAllPetTypes());
    }

    /**
     * Attempts to generate a pet for a given owner.
     * @param request http request containing name of owner, petName, and petType.
     * @return boolean true if pet was generated successfully, else false.
     */
    @PostMapping("/request-a-pet")
    public ResponseEntity<Boolean> getPetForOwner(HttpServletRequest request) {
        String ownerName = request.getParameter("owner");
        String petName = request.getParameter("petName");
        String petType = request.getParameter("petType");
        return ResponseEntity.ok(this.petManagerService.generateANewPetForOwner(ownerName, petName, petType));
    }

    /**
     * Attempts to remove a given pet from the database. Note: only the original owner can remove a pet.
     * @param request http request containing name of owner, and petID.
     * @return boolean true if pet was removed successfully, else false.
     */
    @PostMapping("/abandon-pet")
    public ResponseEntity<Boolean> abandonPet(HttpServletRequest request) {
        String ownerName = request.getParameter("owner");
        int petId = Integer.parseInt(request.getParameter("petId"));
        return ResponseEntity.ok(this.petManagerService.abandonPet(ownerName, petId));
    }

    /**
     * Attempts to get the next or previous shared pets. Should be used to display the pets available for viewing.
     * @param request http request containing a cursor (the current index) and the direction (where to move the cursor)
     * @return a map containing the pets (to be viewed), hasPrevious (if this new view can go to previous),
     * hasNext (if this new view can go next).
     */
    @GetMapping("/get-shared-pets")
    public ResponseEntity<Map<String, Object>> getSharedPets(HttpServletRequest request) {
        String cursorParam = request.getParameter("cursor");
        String direction = request.getParameter("direction");

        if (cursorParam == null || direction == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing 'cursor' or 'direction' parameter"));
        }

        if (!direction.equals("next") && !direction.equals("previous")) {
            return ResponseEntity.badRequest().body(Map.of("error", "'direction' must be 'next' or 'previous'"));
        }

        int cursor;
        try {
            cursor = Integer.parseInt(cursorParam);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "'cursor' must be an integer"));
        }

        Map<String, Object> petsAndCursorStatus = this.petManagerService.getSharedPets(cursor, direction);
        return ResponseEntity.ok(petsAndCursorStatus);
    }

    /**
     * Attempts to share a given pet.
     * @param request http request containing the name of the owner, and the petId.
     * @return boolean true if pet was shared successfully, else false.
     */
    @PostMapping("/share-pet")
    public ResponseEntity<Boolean> sharePet(HttpServletRequest request) {
        String ownerName = request.getParameter("owner");
        int petId = Integer.parseInt(request.getParameter("petId"));
        return ResponseEntity.ok(this.petManagerService.sharePet(ownerName, petId));
    }

    /**
     * Attempts to unshare a given pet.
     * @param request http request containing the name of the owner, and the petId.
     * @return boolean true if pet was unshared successfully, else false.
     */
    @PostMapping("/unshare-pet")
    public ResponseEntity<Boolean> unsharePet(HttpServletRequest request) {
        String ownerName = request.getParameter("owner");
        int petId = Integer.parseInt(request.getParameter("petId"));
        return ResponseEntity.ok(this.petManagerService.unsharePet(ownerName, petId));
    }
}
