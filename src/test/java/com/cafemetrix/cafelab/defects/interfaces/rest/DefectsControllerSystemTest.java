package com.cafemetrix.cafelab.defects.interfaces.rest;

import com.cafemetrix.cafelab.defects.domain.exceptions.DefectNotFoundException;
import com.cafemetrix.cafelab.defects.domain.model.aggregates.Defect;
import com.cafemetrix.cafelab.defects.domain.model.commands.CreateDefectCommand;
import com.cafemetrix.cafelab.defects.domain.services.DefectCommandService;
import com.cafemetrix.cafelab.defects.domain.services.DefectQueryService;
import com.cafemetrix.cafelab.iam.infrastructure.authorization.sfs.support.CurrentProfileIdResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = DefectsController.class,
    excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
@DisplayName("System Tests: DefectsController — /api/v1/defects")
class DefectsControllerSystemTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DefectCommandService defectCommandService;

    @MockBean
    private DefectQueryService defectQueryService;

    @MockBean
    private CurrentProfileIdResolver currentProfileIdResolver;

    // ─── Helper ────────────────────────────────────────────────────────────────

    private Defect mockDefect(Long id, Long userId) {
        var command = new CreateDefectCommand(
            userId, "Café Etiopía", "Yirgacheffe", "Heirloom",
            500.0, "Grano negro", "Categoría 1",
            25.0, 5.0, "Temperatura excesiva durante tueste", "Reducir temperatura a 190°C"
        );
        var defect = spy(new Defect(command));
        doReturn(id).when(defect).getId();
        return defect;
    }

    private Map<String, Object> validCreateBody() {
        return Map.of(
            "coffeeDisplayName", "Café Etiopía",
            "coffeeRegion", "Yirgacheffe",
            "coffeeVariety", "Heirloom",
            "coffeeTotalWeight", 500.0,
            "name", "Grano negro",
            "defectType", "Categoría 1",
            "defectWeight", 25.0,
            "percentage", 5.0,
            "probableCause", "Temperatura excesiva durante tueste",
            "suggestedSolution", "Reducir temperatura a 190°C"
        );
    }

    // ─── POST /api/v1/defects ─────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/v1/defects")
    class CreateDefect {

        @Test
        @DisplayName("Given usuario autenticado y datos válidos, When POST /api/v1/defects, Then responde 201 con el defecto creado")
        void givenAuthenticatedUserAndValidBody_whenPost_thenReturns201() throws Exception {
            when(currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of(1L));
            var defect = mockDefect(10L, 1L);
            when(defectCommandService.handle(any(CreateDefectCommand.class))).thenReturn(Optional.of(defect));

            mockMvc.perform(post("/api/v1/defects")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validCreateBody())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Grano negro"))
                .andExpect(jsonPath("$.defectType").value("Categoría 1"))
                .andExpect(jsonPath("$.coffeeDisplayName").value("Café Etiopía"));
        }

        @Test
        @DisplayName("Given usuario no autenticado, When POST /api/v1/defects, Then responde 401")
        void givenUnauthenticatedUser_whenPost_thenReturns401() throws Exception {
            when(currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.empty());

            mockMvc.perform(post("/api/v1/defects")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validCreateBody())))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Given datos inválidos (peso negativo), When POST /api/v1/defects, Then responde 400")
        void givenInvalidBody_whenPost_thenReturns400() throws Exception {
            when(currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of(1L));

            var invalidBody = Map.of(
                "coffeeDisplayName", "Café Etiopía",
                "name", "Grano negro",
                "defectType", "Categoría 1",
                "defectWeight", -5.0,
                "percentage", 5.0,
                "probableCause", "Temperatura excesiva",
                "suggestedSolution", "Reducir temperatura"
            );

            mockMvc.perform(post("/api/v1/defects")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidBody)))
                .andExpect(status().isBadRequest());
        }
    }

    // ─── GET /api/v1/defects ──────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/v1/defects")
    class GetDefects {

        @Test
        @DisplayName("Given usuario autenticado con 2 defectos, When GET /api/v1/defects, Then responde 200 con lista de 2")
        void givenAuthenticatedUserWithDefects_whenGet_thenReturns200WithList() throws Exception {
            when(currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of(1L));
            var defect1 = mockDefect(1L, 1L);
            var defect2 = mockDefect(2L, 1L);
            when(defectQueryService.handle(any())).thenReturn(List.of(defect1, defect2));

            mockMvc.perform(get("/api/v1/defects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        @DisplayName("Given usuario autenticado sin defectos, When GET /api/v1/defects, Then responde 200 con lista vacía")
        void givenAuthenticatedUserNoDefects_whenGet_thenReturns200WithEmptyList() throws Exception {
            when(currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of(1L));
            when(defectQueryService.handle(any())).thenReturn(List.of());

            mockMvc.perform(get("/api/v1/defects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Given usuario no autenticado, When GET /api/v1/defects, Then responde 401")
        void givenUnauthenticatedUser_whenGet_thenReturns401() throws Exception {
            when(currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/v1/defects"))
                .andExpect(status().isUnauthorized());
        }
    }

    // ─── GET /api/v1/defects/{id} ─────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/v1/defects/{id}")
    class GetDefectById {

        @Test
        @DisplayName("Given defecto existente para el usuario, When GET /api/v1/defects/10, Then responde 200 con el defecto")
        void givenExistingDefect_whenGetById_thenReturns200() throws Exception {
            when(currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of(1L));
            var defect = mockDefect(10L, 1L);
            when(defectQueryService.handle(any())).thenReturn(Optional.of(defect));

            mockMvc.perform(get("/api/v1/defects/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.userId").value(1));
        }

        @Test
        @DisplayName("Given defecto no existente, When GET /api/v1/defects/999, Then responde 404")
        void givenNonExistingDefect_whenGetById_thenReturns404() throws Exception {
            when(currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of(1L));
            when(defectQueryService.handle(any())).thenThrow(new DefectNotFoundException(999L));

            mockMvc.perform(get("/api/v1/defects/999"))
                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Given usuario no autenticado, When GET /api/v1/defects/10, Then responde 401")
        void givenUnauthenticatedUser_whenGetById_thenReturns401() throws Exception {
            when(currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/v1/defects/10"))
                .andExpect(status().isUnauthorized());
        }
    }
}
