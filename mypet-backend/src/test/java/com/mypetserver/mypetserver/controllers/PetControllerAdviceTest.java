package com.mypetserver.mypetserver.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PetControllerAdviceTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String REGISTRATION_URL = "/registration";
    private static final String LOGIN_URL = "/login";

    @Test
    void testWhenRegistrationAllFieldsMissingThenReturnAllErrors() throws Exception {
        String[] expectedErrors = {
                "Username was not received",
                "Display name was not received",
                "Email was not received",
                "Password was not received"
        };

        mockMvc.perform(post(REGISTRATION_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$").value(containsInAnyOrder(expectedErrors)));
    }


    @Test
    void testWhenRegistrationUsernameInvalidThenReturnPatternError() throws Exception {
        String json = """
                {
                    "username": "bad name!",
                    "displayName": "Valid Name",
                    "email": "valid@example.com",
                    "password": "password"
                }
                """;

        mockMvc.perform(post(REGISTRATION_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("Username must contain only alphanumeric characters and underscores"));
    }

    @Test
    void testWhenRegistrationDisplayNameInvalidThenReturnPatternError() throws Exception {
        String json = """
                {
                    "username": "validname",
                    "displayName": "@bad*name!",
                    "email": "valid@example.com",
                    "password": "password"
                }
                """;

        mockMvc.perform(post(REGISTRATION_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("Display name contains invalid characters"));
    }

    @Test
    void testWhenRegistrationEmailInvalidThenReturnEmailError() throws Exception {
        String json = """
                {
                    "username": "validname",
                    "displayName": "Valid Name",
                    "email": "not-an-email",
                    "password": "password"
                }
                """;

        mockMvc.perform(post(REGISTRATION_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("Invalid email received"));
    }

    @Test
    void testWhenRegistrationPasswordTooShortThenReturnAllPasswordErrors() throws Exception {
        String json = """
            {
                "username": "validname",
                "displayName": "Valid Name",
                "email": "valid@example.com",
                "password": ""
            }
            """;

        mockMvc.perform(post(REGISTRATION_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$", containsInAnyOrder(
                        "Password was not received",
                        "Password size not met"
                )));
    }

    @Test
    void testWhenLoginAllFieldsMissingThenReturnBothErrors() throws Exception {
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$").value(containsInAnyOrder(
                        "Username was not received",
                        "Password was not received"
                )));
    }

    @Test
    void testWhenLoginUsernameInvalidThenReturnPatternError() throws Exception {
        String json = """
                {
                    "username": "bad name!",
                    "password": "validPassword"
                }
                """;

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("Username must contain only alphanumeric characters and underscores"));
    }

    @Test
    void testWhenLoginPasswordBlankThenReturnNotBlankError() throws Exception {
        String json = """
                {
                    "username": "validname"
                }
                """;

        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("Password was not received"));
    }
}

