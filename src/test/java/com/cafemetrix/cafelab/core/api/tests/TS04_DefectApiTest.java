package com.cafemetrix.cafelab.core.api.tests;

import com.cafemetrix.cafelab.defects.domain.model.aggregates.Defect;
import com.cafemetrix.cafelab.defects.domain.model.commands.CreateDefectCommand;
import com.cafemetrix.cafelab.defects.domain.model.queries.GetDefectsByUserIdQuery;
import com.cafemetrix.cafelab.defects.domain.services.DefectCommandService;
import com.cafemetrix.cafelab.defects.domain.services.DefectQueryService;
import com.cafemetrix.cafelab.defects.interfaces.rest.DefectsController;
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

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DefectsController.class, 
            excludeAutoConfiguration = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
@DisplayName("TS04 - API Defectos")
class TS04_DefectApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DefectCommandService defectCommandService;

    @MockitoBean
    private DefectQueryService defectQueryService;

    @MockitoBean
    private CurrentProfileIdResolver currentProfileIdResolver;

    @Test
    @DisplayName("POST /api/v1/defects crea un defecto exitosamente (201)")
    void createDefectReturnsCreated() throws Exception {
        when(currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of(7L));
        
        var defect = new Defect(new CreateDefectCommand(
                7L, "Lote 1", "Jaen", "Caturra", 50.0, "Scorching", "Roast", 5.0, 10.0, "Cause", "Solution"
        ));
        when(defectCommandService.handle(any(CreateDefectCommand.class))).thenReturn(Optional.of(defect));

        String json = """
                {
                  "coffeeDisplayName": "Lote 1",
                  "coffeeRegion": "Jaen",
                  "coffeeVariety": "Caturra",
                  "coffeeTotalWeight": 50.0,
                  "name": "Scorching",
                  "defectType": "Roast",
                  "defectWeight": 5.0,
                  "percentage": 10.0,
                  "probableCause": "Cause",
                  "suggestedSolution": "Solution"
                }
                """;

        mockMvc.perform(post("/api/v1/defects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Scorching"));
    }

    @Test
    @DisplayName("GET /api/v1/defects retorna lista de defectos (200)")
    void getAllDefectsReturnsOk() throws Exception {
        when(currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of(7L));
        // Resolución de ambigüedad
        when(defectQueryService.handle(any(GetDefectsByUserIdQuery.class))).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/defects"))
                .andExpect(status().isOk());
    }
}
