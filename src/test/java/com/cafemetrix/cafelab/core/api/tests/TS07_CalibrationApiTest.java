package com.cafemetrix.cafelab.core.api.tests;

import com.cafemetrix.cafelab.calibrations.domain.model.aggregates.GrindCalibration;
import com.cafemetrix.cafelab.calibrations.domain.model.commands.CreateGrindCalibrationCommand;
import com.cafemetrix.cafelab.calibrations.domain.model.queries.GetGrindCalibrationsByUserIdQuery;
import com.cafemetrix.cafelab.calibrations.domain.services.GrindCalibrationCommandService;
import com.cafemetrix.cafelab.calibrations.domain.services.GrindCalibrationQueryService;
import com.cafemetrix.cafelab.calibrations.interfaces.rest.CalibrationsController;
import com.cafemetrix.cafelab.iam.infrastructure.authorization.sfs.support.CurrentProfileIdResolver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CalibrationsController.class, 
            excludeAutoConfiguration = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
@DisplayName("TS07 - API Calibraciones")
class TS07_CalibrationApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GrindCalibrationCommandService commandService;

    @MockitoBean
    private GrindCalibrationQueryService queryService;

    @MockitoBean
    private CurrentProfileIdResolver currentProfileIdResolver;

    @Test
    @DisplayName("POST /api/v1/calibrations crea una calibración exitosamente (201)")
    void createCalibrationReturnsCreated() throws Exception {
        when(currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of(7L));
        
        var calibration = new GrindCalibration(new CreateGrindCalibrationCommand(
                7L, "V60 Test", "V60", "Equipment", "20", 500.0, 250.0, 200.0, LocalDate.now(), "Comments", "Notes", "url"
        ));
        // Resolución de ambigüedad
        when(commandService.handle(any(CreateGrindCalibrationCommand.class))).thenReturn(Optional.of(calibration));

        String json = """
                {
                  "name": "V60 Test",
                  "method": "V60",
                  "equipment": "Equipment",
                  "grindNumber": "20",
                  "aperture": 500.0,
                  "cupVolume": 250.0,
                  "finalVolume": 200.0,
                  "calibrationDate": "2026-05-12",
                  "comments": "Comments",
                  "notes": "Notes",
                  "sampleImage": "url"
                }
                """;

        mockMvc.perform(post("/api/v1/calibrations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("V60 Test"));
    }

    @Test
    @DisplayName("GET /api/v1/calibrations retorna lista de calibraciones (200)")
    void getAllCalibrationsReturnsOk() throws Exception {
        when(currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of(7L));
        // Resolución de ambigüedad
        when(queryService.handle(any(GetGrindCalibrationsByUserIdQuery.class))).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/calibrations"))
                .andExpect(status().isOk());
    }
}
