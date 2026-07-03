package com.cafemetrix.cafelab.bdd.steps;

import com.cafemetrix.cafelab.cuppingsessions.domain.model.aggregates.CuppingSession;
import com.cafemetrix.cafelab.cuppingsessions.domain.model.commands.CreateCuppingSessionCommand;
import com.cafemetrix.cafelab.cuppingsessions.domain.model.queries.GetCuppingSessionsByUserIdQuery;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class CuppingSessionSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SharedSteps shared;

    @Given("un barista autenticado con perfil id {int}")
    public void unBaristaAutenticadoConPerfilId(int profileId) {
        when(shared.currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of((long) profileId));
    }

    @Given("un barista autenticado con perfil id {int} y {int} catas registradas")
    public void unBaristaAutenticadoConPerfilIdYCatasRegistradas(int profileId, int cantidad) {
        when(shared.currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of((long) profileId));
        var sessions = List.of(mockSession(1L, profileId), mockSession(2L, profileId)).subList(0, cantidad);
        when(shared.cuppingSessionQueryService.handle(any(GetCuppingSessionsByUserIdQuery.class))).thenReturn(sessions);
    }

    @When("envía una solicitud para crear una cata con nombre {string} origen {string} variedad {string} procesamiento {string} fecha {string}")
    public void enviaSolicitudCrearCata(String nombre, String origen, String variedad, String procesamiento, String fecha) throws Exception {
        var mockSession = mockSession(5L, 1);
        when(shared.cuppingSessionCommandService.handle(any(CreateCuppingSessionCommand.class)))
                .thenReturn(Optional.of(mockSession));

        var body = Map.of(
                "name", nombre,
                "origin", origen,
                "variety", variedad,
                "processing", procesamiento,
                "sessionDate", fecha,
                "favorite", false
        );
        shared.lastResult = mockMvc.perform(post("/api/v1/cupping-sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    @When("envía una solicitud para listar sus sesiones de cata")
    public void enviaSolicitudListarCatas() throws Exception {
        shared.lastResult = mockMvc.perform(get("/api/v1/cupping-sessions"));
    }

    @And("la respuesta contiene el id de la sesión de cata")
    public void laRespuestaContieneElIdDeLaSesionDeCata() throws Exception {
        shared.lastResult.andExpect(jsonPath("$.id").isNotEmpty());
    }

    @And("la respuesta contiene una lista con {int} sesiones")
    public void laRespuestaContieneUnaListaConSesiones(int cantidad) throws Exception {
        shared.lastResult.andExpect(jsonPath("$.length()").value(cantidad));
    }

    private CuppingSession mockSession(Long id, int userId) {
        var command = new CreateCuppingSessionCommand(
                (long) userId, "Cata Test", "Etiopía", "Heirloom",
                "Natural", LocalDate.of(2026, 5, 12), false, null, null, null, null
        );
        var session = spy(new CuppingSession(command));
        doReturn(id).when(session).getId();
        return session;
    }
}
