package com.mypetserver.mypetserver.controllers;

import com.mypetserver.mypetserver.dto.LoginRequest;
import com.mypetserver.mypetserver.dto.LoginResponse;
import com.mypetserver.mypetserver.services.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * This Class defines RESTful APIs for requests to the server
 */
@RestController
public class PetController {
    private static final Logger logger = LoggerFactory.getLogger(PetController.class);

    private final AuthenticationService authService;

    @Autowired
    public PetController(AuthenticationService authService) {
        this.authService = authService;
    }

    // Post requests
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            //System.out.println("Login request: " + loginRequest.getUsername() + " " + loginRequest.getPassword());
            String token = authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
            return ResponseEntity.ok(new LoginResponse(loginRequest.getUsername(), token));
        } catch (Exception e) {
            logger.error("Error: could not login user {} - {}", loginRequest.getUsername(),e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).contentType(MediaType.APPLICATION_JSON).build();
        }

    }

    //For future reference TODO: Remove when another request has been added
//    @PostMapping("/hello")
//    public ResponseEntity<String> hello(@RequestBody Map<String, Object> requestBody) {
//        try {
//            String name = (String) requestBody.get("name");
//            return ResponseEntity.ok()
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body("{\"message\": \"Hello " + name + "\"}");
//        } catch (Exception e) {
//            logger.error("Error: could not process hello request - {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//
//    }
}
