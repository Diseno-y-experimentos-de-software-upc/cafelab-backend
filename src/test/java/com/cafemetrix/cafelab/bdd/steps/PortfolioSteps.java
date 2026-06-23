package com.cafemetrix.cafelab.bdd.steps;

import com.cafemetrix.cafelab.preparation.domain.model.aggregates.Portfolio;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class PortfolioSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SharedSteps shared;

    @Given("un barista autenticado con perfil id {int} y portafolio listo para crear")
    public void unBaristaAutenticadoConPerfilIdYPortafolioListoParaCrear(int profileId) {
        when(shared.currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of((long) profileId));
        when(shared.preparationContextFacade.createPortfolio(anyLong(), anyString())).thenReturn(70L);
        var mockPortfolio = mock(Portfolio.class);
        when(mockPortfolio.getId()).thenReturn(70L);
        when(mockPortfolio.getUserId()).thenReturn((long) profileId);
        when(mockPortfolio.getName()).thenReturn("Espressos Clasicos");
        when(mockPortfolio.getCreatedAt()).thenReturn(new Date());
        when(shared.preparationContextFacade.getPortfolioByIdForUser(anyLong(), anyLong())).thenReturn(Optional.of(mockPortfolio));
    }

    @Given("un barista autenticado con perfil id {int} pero creación de portafolio falla")
    public void unBaristaAutenticadoPeroCreacionDePortafolioFalla(int profileId) {
        when(shared.currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of((long) profileId));
        when(shared.preparationContextFacade.createPortfolio(anyLong(), anyString())).thenReturn(0L);
    }

    @When("envía una solicitud para crear un portafolio con nombre {string}")
    public void enviaSolicitudCrearPortafolio(String nombre) throws Exception {
        var body = Map.of("name", nombre);
        shared.lastResult = mockMvc.perform(post("/api/v1/portfolios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    @And("la respuesta contiene el id del portafolio creado")
    public void laRespuestaContieneElIdDelPortafolioCreado() throws Exception {
        shared.lastResult.andExpect(jsonPath("$.id").isNotEmpty());
    }
}
