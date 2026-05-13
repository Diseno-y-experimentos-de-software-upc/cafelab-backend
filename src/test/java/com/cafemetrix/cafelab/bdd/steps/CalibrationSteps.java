package com.cafemetrix.cafelab.bdd.steps;

import com.cafemetrix.cafelab.calibrations.domain.model.aggregates.GrindCalibration;
import com.cafemetrix.cafelab.calibrations.domain.model.commands.CreateGrindCalibrationCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class CalibrationSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SharedSteps shared;

    @Given("un barista autenticado con perfil id {int} y calibración lista para crear")
    public void unBaristaAutenticadoConPerfilIdYCalibracionListaParaCrear(int profileId) {
        when(shared.currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of((long) profileId));
        var mockCalibration = mock(GrindCalibration.class);
        when(mockCalibration.getId()).thenReturn(60L);
        when(mockCalibration.getUserId()).thenReturn((long) profileId);
        when(shared.grindCalibrationCommandService.handle(any(CreateGrindCalibrationCommand.class))).thenReturn(Optional.of(mockCalibration));
    }

    @Given("un barista autenticado con perfil id {int} pero creación de calibración falla")
    public void unBaristaAutenticadoPeroCreacionDeCalibracionFalla(int profileId) {
        when(shared.currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of((long) profileId));
        when(shared.grindCalibrationCommandService.handle(any(CreateGrindCalibrationCommand.class))).thenReturn(Optional.empty());
    }

    @When("envía una solicitud para crear una calibración con nombre {string} metodo {string} equipo {string} numero {string}")
    public void enviaSolicitudCrearCalibracion(String nombre, String metodo, String equipo, String numero) throws Exception {
        var body = Map.of(
                "name", nombre,
                "method", metodo,
                "equipment", equipo,
                "grindNumber", numero,
                "aperture", 0.5,
                "cupVolume", 30.0,
                "finalVolume", 28.0,
                "calibrationDate", LocalDate.now().toString()
        );
        shared.lastResult = mockMvc.perform(post("/api/v1/calibrations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    @And("la respuesta contiene el id de la calibración creada")
    public void laRespuestaContieneElIdDeLaCalibracionCreada() throws Exception {
        shared.lastResult.andExpect(jsonPath("$.id").isNotEmpty());
    }
}
