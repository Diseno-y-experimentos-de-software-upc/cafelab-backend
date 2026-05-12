package com.cafemetrix.cafelab.core.api.tests;

import com.cafemetrix.cafelab.iam.domain.model.aggregates.User;
import com.cafemetrix.cafelab.iam.domain.services.UserCommandService;
import com.cafemetrix.cafelab.iam.interfaces.rest.AuthenticationController;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthenticationController.class, 
            excludeAutoConfiguration = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
@DisplayName("TS09 - API Registro de Usuarios")
class TS09_UserRegistrationApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserCommandService userCommandService;

    @Test
    @DisplayName("POST /api/v1/authentication/sign-up crea un usuario exitosamente (201)")
    void signUpReturnsCreated() throws Exception {
        var user = new User("test@test.com", "password123", "ROLE_USER");
        var token = "mock-jwt-token";
        
        when(userCommandService.handle(any(com.cafemetrix.cafelab.iam.domain.model.commands.SignUpCommand.class)))
                .thenReturn(Optional.of(user));
        
        when(userCommandService.handle(any(com.cafemetrix.cafelab.iam.domain.model.commands.SignInCommand.class)))
                .thenReturn(Optional.of(new ImmutablePair<>(user, token)));

        String json = """
                {
                  "email": "test@test.com",
                  "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/v1/authentication/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.token").value("mock-jwt-token"));
    }
}
