package com.mypetserver.mypetserver.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.Objects;

/**
 * Defines LoginRequest object class for representing login requests that come in.
 * Utilizes Spring Boots Jackson deserialization when the object is coming from the front end
 */
@Getter
public class LoginRequest {

    @NotBlank(message = "Username was not received")
    @Size(min = 1, max = 255, message = "Username length not met")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username must contain only alphanumeric characters and underscores")
    private final String username;

    @NotBlank(message = "Password was not received")
    @Size(min = 1, max = 255, message = "Password size not met")
    private final String password;

    @JsonCreator
    public LoginRequest(@JsonProperty("username") String username, @JsonProperty("password") String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public boolean equals(Object otherLoginRequestObj) {
        if (this == otherLoginRequestObj) {
            return true;
        }

        else if (otherLoginRequestObj == null || this.getClass() != otherLoginRequestObj.getClass()) {
            return false;
        }

        else {
            LoginRequest otherLoginReq = (LoginRequest) otherLoginRequestObj;
            return this.username.equals(otherLoginReq.username) &&
                    this.password.equals(otherLoginReq.password);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.username, this.password);
    }

}
