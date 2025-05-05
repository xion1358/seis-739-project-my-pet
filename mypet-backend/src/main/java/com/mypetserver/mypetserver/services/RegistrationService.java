package com.mypetserver.mypetserver.services;

import com.mypetserver.mypetserver.entities.Owner;
import com.mypetserver.mypetserver.dto.RegistrationRequest;
import com.mypetserver.mypetserver.dto.RegistrationResponse;
import com.mypetserver.mypetserver.repository.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {
    private final OwnerService ownerService;
    private final TokenService tokenService;
    private final OwnerRepository ownerRepository;
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    public RegistrationService(OwnerService ownerService,
                               TokenService tokenService,
                               OwnerRepository ownerRepository) {
        this.ownerService = ownerService;
        this.tokenService = tokenService;
        this.ownerRepository = ownerRepository;
    }

    public ResponseEntity<RegistrationResponse> register(RegistrationRequest registrationRequest) {

        if (this.ownerRepository.getOwnerByUsername(registrationRequest.getUsername()) != null ||
            this.ownerRepository.getOwnerByEmail(registrationRequest.getEmail()) != null) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new RegistrationResponse(
                            registrationRequest.getUsername(),
                            null,
                            "Username or Email already taken. Please try again."
                    ));
        } else {
            this.ownerService.saveOwner(new Owner(
                    registrationRequest.getUsername(),
                    registrationRequest.getDisplayName(),
                    registrationRequest.getEmail(),
                    encoder.encode(registrationRequest.getPassword())
            ));

            return ResponseEntity.ok(new RegistrationResponse(
                    registrationRequest.getUsername(),
                    this.tokenService.generateToken(registrationRequest.getUsername()),
                    "Registered Successfully"));
        }

    }
}
