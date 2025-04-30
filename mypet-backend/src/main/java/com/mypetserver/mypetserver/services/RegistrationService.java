package com.mypetserver.mypetserver.services;

import com.mypetserver.mypetserver.entities.Owner;
import com.mypetserver.mypetserver.dto.RegistrationRequest;
import com.mypetserver.mypetserver.dto.RegistrationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {
    private final OwnerService ownerService;
    private final TokenService tokenService;
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    public RegistrationService(OwnerService ownerService, TokenService tokenService) {
        this.ownerService = ownerService;
        this.tokenService = tokenService;
    }

    public ResponseEntity<RegistrationResponse> register(RegistrationRequest registrationRequest) {
        this.ownerService.saveOwner(new Owner(
                registrationRequest.getUsername(),
                registrationRequest.getDisplayName(),
                registrationRequest.getEmail(),
                encoder.encode(registrationRequest.getPassword())
                ));

        return ResponseEntity.ok(new RegistrationResponse(
                registrationRequest.getUsername(),
                this.tokenService.generateToken(registrationRequest.getUsername())));
    }
}
