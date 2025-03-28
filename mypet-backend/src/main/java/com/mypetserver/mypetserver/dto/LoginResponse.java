package com.mypetserver.mypetserver.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Objects;

/**
 * Defines LoginResponse object class for representing login requests that come in.
 * Utilizes Spring Boots Jackson serialization when the object is returned to the front end
 */
@Getter
public class LoginResponse {
    private final String username;
    private final String token;

    @JsonCreator
    public LoginResponse(@JsonProperty("username") String username, @JsonProperty("token") String token) {
        this.username = username;
        this.token = token;
    }

    @Override
    public boolean equals(Object otherLoginResponseObj) {
        if (this == otherLoginResponseObj) {
            return true;
        }

        else if (otherLoginResponseObj == null || this.getClass() != otherLoginResponseObj.getClass()) {
            return false;
        }

        else {
            LoginResponse otherLoginRes = (LoginResponse) otherLoginResponseObj;
            return this.username.equals(otherLoginRes.username) &&
                    this.token.equals(otherLoginRes.token);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.username, this.token);
    }

}
