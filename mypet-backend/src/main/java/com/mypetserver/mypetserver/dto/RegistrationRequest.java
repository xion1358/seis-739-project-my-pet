package com.mypetserver.mypetserver.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.Objects;

/**
 * Defines RegistrationRequest object class for representing registration requests that come in.
 * Utilizes Spring Boots Jackson deserialization when the object is coming from the front end
 */
@Getter
public class RegistrationRequest {

    @NotBlank(message = "Username was not received")
    @Size(min = 1, max = 255, message = "Username length not met")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username must contain only alphanumeric characters and underscores")
    private final String username;

    @NotBlank(message = "Display name was not received")
    @Size(min = 1, max = 255, message = "Display name length not met")
    @Pattern(regexp = "^[a-zA-Z0-9\\p{L} \\p{M}.'-]+$", message = "Display name contains invalid characters")
    private final String displayName;

    @NotBlank(message = "Email was not received")
    @Email(message = "Invalid email received")
    @Size(min = 1, max = 255, message = "Email size not met")
    private final String email;

    @NotBlank(message = "Password was not received")
    @Size(min = 1, max = 255, message = "Password size not met")
    private final String password;

    @JsonCreator
    public RegistrationRequest(@JsonProperty("username") String username,
                               @JsonProperty("displayName") String displayName,
                               @JsonProperty("email") String email,
                               @JsonProperty("password") String password) {
        this.username = username;
        this.displayName = displayName;
        this.email = email;
        this.password = password;
    }

    @Override
    public boolean equals(Object otherRegistrationRequestObj) {
        if (this == otherRegistrationRequestObj) {
            return true;
        }

        else if (otherRegistrationRequestObj == null || this.getClass() != otherRegistrationRequestObj.getClass()) {
            return false;
        }

        else {
            RegistrationRequest otherRegistrationReq = (RegistrationRequest) otherRegistrationRequestObj;
            return  this.username.equals(otherRegistrationReq.username) &&
                    this.displayName.equals(otherRegistrationReq.displayName) &&
                    this.email.equals(otherRegistrationReq.email) &&
                    this.password.equals(otherRegistrationReq.password);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.username, this.displayName, this.email, this.password);
    }
}
