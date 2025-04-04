package com.mypetserver.mypetserver.controllers;

import com.mypetserver.mypetserver.dto.*;
import com.mypetserver.mypetserver.services.LoginService;
import com.mypetserver.mypetserver.services.RegistrationService;
import com.mypetserver.mypetserver.services.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * This Class defines RESTful APIs for requests to the server
 */
@RestController
public class PetController {
    private static final Logger logger = LoggerFactory.getLogger(PetController.class);

    private final LoginService loginService;
    private final RegistrationService registrationService;
    private final TokenService tokenService;

    @Autowired
    public PetController(
            LoginService loginService,
            RegistrationService registrationService,
            TokenService tokenService) {
        this.loginService = loginService;
        this.registrationService = registrationService;
        this.tokenService = tokenService;
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

    @PostMapping("/validate")
    public ResponseEntity<Void> validate(HttpServletRequest request) {
        return ResponseEntity.ok().build();
    }
}
