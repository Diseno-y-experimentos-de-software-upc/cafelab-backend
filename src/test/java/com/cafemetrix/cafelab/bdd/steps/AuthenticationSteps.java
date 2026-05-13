package com.cafemetrix.cafelab.bdd.steps;

import com.cafemetrix.cafelab.iam.domain.model.aggregates.User;
import com.cafemetrix.cafelab.iam.domain.model.commands.SignInCommand;
import com.cafemetrix.cafelab.iam.domain.model.commands.SignUpCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class AuthenticationSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SharedSteps shared;

    private String email;
    private String password;

    @Given("un usuario con email {string} y contraseña {string}")
    public void unUsuarioConEmailYContrasena(String email, String password) {
        this.email = email;
        this.password = password;
        var mockUser = new User(email, "hashed", null);
        when(shared.userCommandService.handle(any(SignUpCommand.class))).thenReturn(Optional.of(mockUser));
        when(shared.userCommandService.handle(any(SignInCommand.class)))
                .thenReturn(Optional.of(Pair.of(mockUser, "mock-jwt-token")));
    }

    @Given("un usuario registrado con email {string} y contraseña {string}")
    public void unUsuarioRegistradoConEmailYContrasena(String email, String password) {
        this.email = email;
        this.password = password;
        var mockUser = new User(email, "hashed", null);
        when(shared.userCommandService.handle(any(SignInCommand.class)))
                .thenReturn(Optional.of(Pair.of(mockUser, "mock-jwt-token")));
    }

    @Given("un usuario no registrado con email {string} y contraseña {string}")
    public void unUsuarioNoRegistradoConEmailYContrasena(String email, String password) {
        this.email = email;
        this.password = password;
        when(shared.userCommandService.handle(any(SignInCommand.class))).thenReturn(Optional.empty());
    }

    @When("el usuario envía una solicitud de registro")
    public void elUsuarioEnviaUnaSolicitudDeRegistro() throws Exception {
        var body = Map.of("email", email, "password", password);
        shared.lastResult = mockMvc.perform(post("/api/v1/authentication/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    @When("el usuario envía una solicitud de inicio de sesión")
    public void elUsuarioEnviaUnaSolicitudDeInicioSesion() throws Exception {
        var body = Map.of("email", email, "password", password);
        shared.lastResult = mockMvc.perform(post("/api/v1/authentication/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    @And("la respuesta contiene un token de acceso")
    public void laRespuestaContieneUnTokenDeAcceso() throws Exception {
        shared.lastResult.andExpect(jsonPath("$.token").isNotEmpty());
    }
}
