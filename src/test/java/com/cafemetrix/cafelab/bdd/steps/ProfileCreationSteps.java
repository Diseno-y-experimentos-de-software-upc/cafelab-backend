package com.cafemetrix.cafelab.bdd.steps;

import com.cafemetrix.cafelab.profiles.domain.model.aggregates.Profile;
import com.cafemetrix.cafelab.profiles.domain.model.commands.CreateProfileCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class ProfileCreationSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SharedSteps shared;

    @Given("un perfil listo para crear con rol barista")
    public void unPerfilListoParaCrearConRolBarista() {
        var mockProfile = mock(Profile.class);
        when(mockProfile.getId()).thenReturn(100L);
        when(mockProfile.getName()).thenReturn("Juan Barista");
        when(mockProfile.getEmailAddress()).thenReturn("juan@cafelab.com");
        when(mockProfile.getRole()).thenReturn("barista");
        when(mockProfile.getCafeteriaName()).thenReturn("Cafe Test");
        when(shared.profileCommandService.handle(any(CreateProfileCommand.class))).thenReturn(Optional.of(mockProfile));
    }

    @Given("un perfil que falla al crearse")
    public void unPerfilQueFallaAlCrearse() {
        when(shared.profileCommandService.handle(any(CreateProfileCommand.class))).thenReturn(Optional.empty());
    }

    @When("envía una solicitud para crear un perfil con nombre {string} email {string} rol {string}")
    public void enviaSolicitudCrearPerfil(String nombre, String email, String rol) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("name", nombre);
        body.put("email", email);
        body.put("password", "password123");
        body.put("role", rol);
        body.put("cafeteriaName", "Cafe Test");
        body.put("experience", "5 anios");
        body.put("profilePicture", "");
        body.put("paymentMethod", "efectivo");
        body.put("isFirstLogin", true);
        body.put("plan", "basic");
        body.put("hasPlan", false);
        shared.lastResult = mockMvc.perform(post("/api/v1/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    @And("la respuesta contiene el id del perfil creado")
    public void laRespuestaContieneElIdDelPerfilCreado() throws Exception {
        shared.lastResult.andExpect(jsonPath("$.id").isNotEmpty());
    }
}
