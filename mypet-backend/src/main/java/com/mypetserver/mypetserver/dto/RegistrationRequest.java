package com.mypetserver.mypetserver.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Objects;

@Getter
public class RegistrationRequest {
    private final String username;
    private final String displayName;
    private final String email;
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
