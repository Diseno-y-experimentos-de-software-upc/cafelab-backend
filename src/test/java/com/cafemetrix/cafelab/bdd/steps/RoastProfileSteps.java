package com.cafemetrix.cafelab.bdd.steps;

import com.cafemetrix.cafelab.production.domain.model.aggregates.CoffeeLot;
import com.cafemetrix.cafelab.production.domain.model.aggregates.RoastProfile;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class RoastProfileSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SharedSteps shared;

    private Long lotId;

    @Given("un barista autenticado con perfil id {int} y lote id {int} disponible")
    public void unBaristaAutenticadoConPerfilIdYLoteIdDisponible(int profileId, int lotId) {
        this.lotId = (long) lotId;
        when(shared.currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of((long) profileId));
        var mockLot = mock(CoffeeLot.class);
        when(mockLot.getUserId()).thenReturn((long) profileId);
        when(shared.coffeeproductionContextFacade.getCoffeeLotById(this.lotId)).thenReturn(Optional.of(mockLot));
        when(shared.coffeeproductionContextFacade.createRoastProfile(anyLong(), anyString(), anyString(), anyInt(), anyDouble(), anyDouble(), anyLong(), anyBoolean()))
                .thenReturn(10L);
        var mockProfile = mock(RoastProfile.class);
        when(mockProfile.getId()).thenReturn(10L);
        when(mockProfile.getUserId()).thenReturn((long) profileId);
        when(shared.coffeeproductionContextFacade.getRoastProfileById(10L)).thenReturn(Optional.of(mockProfile));
    }

    @Given("un barista autenticado con perfil id {int} pero sin acceso al lote id {int}")
    public void unBaristaAutenticadoPeroSinAccesoAlLote(int profileId, int lotId) {
        this.lotId = (long) lotId;
        when(shared.currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of((long) profileId));
        when(shared.coffeeproductionContextFacade.getCoffeeLotById(this.lotId)).thenReturn(Optional.empty());
    }

    @When("envía una solicitud para crear un perfil con nombre {string} tipo {string} duración {int} tempInicio {double} tempFin {double}")
    public void enviaSolicitudCrearPerfil(String nombre, String tipo, int duracion, double tempInicio, double tempFin) throws Exception {
        var body = Map.of(
                "name", nombre,
                "type", tipo,
                "duration", duracion,
                "tempStart", (int) tempInicio,
                "tempEnd", (int) tempFin,
                "lot", lotId != null ? lotId : 99L,
                "isFavorite", false
        );
        shared.lastResult = mockMvc.perform(post("/api/v1/roast-profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print());
    }

    @And("la respuesta contiene el id del perfil de tueste creado")
    public void laRespuestaContieneElIdDelPerfilDeTuesteCreado() throws Exception {
        shared.lastResult.andExpect(jsonPath("$.id").isNotEmpty());
    }
}
