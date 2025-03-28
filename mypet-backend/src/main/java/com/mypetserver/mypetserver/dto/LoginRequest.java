package com.mypetserver.mypetserver.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Objects;

/**
 * Defines LoginRequest object class for representing login requests that come in.
 * Utilizes Spring Boots Jackson deserialization when the object is coming from the front end
 */
@Getter
public class LoginRequest {
    private final String username;
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
