package com.mypetserver.mypetserver.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Objects;

/**
 * Defines RegistrationResponse object class for representing registration requests that go out.
 * Utilizes Spring Boots Jackson serialization when the object going to the front end
 */
@Getter
public class RegistrationResponse {
    private final String username;
    private final String token;
    private final String message;

    @JsonCreator
    public RegistrationResponse(@JsonProperty("username") String username,
                                @JsonProperty("token") String token,
                                @JsonProperty("message") String message) {
        this.username = username;
        this.token = token;
        this.message = message;
    }

    @Override
    public boolean equals(Object otherRegistrationResponseObj) {
        if (this == otherRegistrationResponseObj) {
            return true;
        }

        else if (otherRegistrationResponseObj == null || this.getClass() != otherRegistrationResponseObj.getClass()) {
            return false;
        }

        else {
            RegistrationResponse otherRegistrationRes = (RegistrationResponse) otherRegistrationResponseObj;
            return this.username.equals(otherRegistrationRes.username) &&
                    this.token.equals(otherRegistrationRes.token);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.username, this.token);
    }
}
