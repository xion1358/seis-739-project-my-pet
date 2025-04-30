package com.mypetserver.mypetserver.controllers;

import com.mypetserver.mypetserver.dto.*;
import com.mypetserver.mypetserver.entities.Pet;
import com.mypetserver.mypetserver.services.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This Class defines RESTful APIs for requests to the server
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

    // Post requests
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            return this.loginService.login(loginRequest);
        } catch (Exception e) {
            logger.error("Error: could not login user {} - {}", loginRequest.getUsername(),e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).contentType(MediaType.APPLICATION_JSON).build();
        }

    }

    @PostMapping("/registration")
    public ResponseEntity<RegistrationResponse> register(@RequestBody RegistrationRequest registrationRequest) {
        try {
            return this.registrationService.register(registrationRequest);
        } catch (Exception e) {
            logger.error("Error: could not register user {} - {}", registrationRequest.getUsername(),e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).build();
        }
    }

    // Simply return ok as the filter will handle the validation when the request comes in
    @PostMapping("/validate")
    public ResponseEntity<Void> validate(HttpServletRequest request) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get-pets")
    public ResponseEntity<List<Pet>> getPets(HttpServletRequest request) {
        ArrayList<Pet> pets = this.petManagerService.getPets(request.getParameter("owner"));
        return ResponseEntity.ok(pets);
    }

    @PostMapping("/register-pet-for-viewing")
    public ResponseEntity<Pet> registerPetForViewing(HttpServletRequest request) {
        Pet registeredPet = this.petManagerService.registerPet(Integer.parseInt(request.getParameter("id")));
        return ResponseEntity.ok(registeredPet);
    }

    @PostMapping("/create-pet-food")
    public ResponseEntity<Boolean> createPetFood(HttpServletRequest request) {
        logger.info("Creating pet food for {}", request.getParameter("id"));
        return ResponseEntity.ok(this.petFoodService.createPetFood(Integer.parseInt(request.getParameter("id")), request.getParameter("food")));
    }

    @PostMapping("/pet-a-pet")
    public ResponseEntity<Boolean> petAPet(HttpServletRequest request) {
        logger.info("Petting pet {}", request.getParameter("id"));
        return ResponseEntity.ok(this.petPlayService.petAPet(Integer.parseInt(request.getParameter("id"))));
    }
}
