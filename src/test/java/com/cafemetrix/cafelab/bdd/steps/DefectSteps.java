package com.cafemetrix.cafelab.bdd.steps;

import com.cafemetrix.cafelab.defects.domain.model.aggregates.Defect;
import com.cafemetrix.cafelab.defects.domain.model.commands.CreateDefectCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class DefectSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SharedSteps shared;

    @Given("un barista autenticado con perfil id {int} y defecto listo para crear")
    public void unBaristaAutenticadoConPerfilIdYDefectoListoParaCrear(int profileId) {
        when(shared.currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of((long) profileId));
        var mockDefect = mock(Defect.class);
        when(mockDefect.getId()).thenReturn(40L);
        when(mockDefect.getUserId()).thenReturn((long) profileId);
        when(shared.defectCommandService.handle(any(CreateDefectCommand.class))).thenReturn(Optional.of(mockDefect));
    }

    @Given("un barista autenticado con perfil id {int} pero creación de defecto falla")
    public void unBaristaAutenticadoPeroCreacionDeDefectoFalla(int profileId) {
        when(shared.currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of((long) profileId));
        when(shared.defectCommandService.handle(any(CreateDefectCommand.class))).thenReturn(Optional.empty());
    }

    @When("envía una solicitud para crear un defecto de café {string} tipo {string} peso {double} porcentaje {double}")
    public void enviaSolicitudCrearDefecto(String cafeName, String tipo, double peso, double porcentaje) throws Exception {
        var body = Map.of(
                "coffeeDisplayName", cafeName,
                "coffeeRegion", "Yirgacheffe",
                "coffeeVariety", "Heirloom",
                "coffeeTotalWeight", 50.0,
                "name", "Defecto " + tipo,
                "defectType", tipo,
                "defectWeight", peso,
                "percentage", porcentaje,
                "probableCause", "Secado irregular",
                "suggestedSolution", "Mejorar proceso de secado"
        );
        shared.lastResult = mockMvc.perform(post("/api/v1/defects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    @And("la respuesta contiene el id del defecto creado")
    public void laRespuestaContieneElIdDelDefectoCreado() throws Exception {
        shared.lastResult.andExpect(jsonPath("$.id").isNotEmpty());
    }
}
