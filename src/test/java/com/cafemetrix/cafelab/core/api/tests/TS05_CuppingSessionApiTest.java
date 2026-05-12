package com.cafemetrix.cafelab.core.api.tests;

import com.cafemetrix.cafelab.cuppingsessions.domain.model.aggregates.CuppingSession;
import com.cafemetrix.cafelab.cuppingsessions.domain.model.commands.CreateCuppingSessionCommand;
import com.cafemetrix.cafelab.cuppingsessions.domain.model.queries.GetCuppingSessionsByUserIdQuery;
import com.cafemetrix.cafelab.cuppingsessions.domain.services.CuppingSessionCommandService;
import com.cafemetrix.cafelab.cuppingsessions.domain.services.CuppingSessionQueryService;
import com.cafemetrix.cafelab.cuppingsessions.interfaces.rest.CuppingSessionsController;
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

@WebMvcTest(controllers = CuppingSessionsController.class, 
            excludeAutoConfiguration = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
@DisplayName("TS05 - API Catas")
class TS05_CuppingSessionApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CuppingSessionCommandService commandService;

    @MockitoBean
    private CuppingSessionQueryService queryService;

    @MockitoBean
    private CurrentProfileIdResolver currentProfileIdResolver;

    @Test
    @DisplayName("POST /api/v1/cupping-sessions crea una sesión de cata exitosamente (201)")
    void createCuppingSessionReturnsCreated() throws Exception {
        when(currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of(7L));
        
        var session = new CuppingSession(new CreateCuppingSessionCommand(
                7L, "Cupping Geisha", "Chiriqui", "Geisha", "Lavado", LocalDate.now(), true, "{}", "Notes"
        ));
        // Resolución de ambigüedad
        when(commandService.handle(any(CreateCuppingSessionCommand.class))).thenReturn(Optional.of(session));

        String json = """
                {
                  "name": "Cupping Geisha",
                  "origin": "Chiriqui",
                  "variety": "Geisha",
                  "processing": "Lavado",
                  "sessionDate": "2026-05-12",
                  "favorite": true,
                  "resultsJson": "{}",
                  "roastStyleNotes": "Notes"
                }
                """;

        mockMvc.perform(post("/api/v1/cupping-sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Cupping Geisha"));
    }

    @Test
    @DisplayName("GET /api/v1/cupping-sessions retorna lista de catas (200)")
    void getAllCuppingSessionsReturnsOk() throws Exception {
        when(currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of(7L));
        // Resolución de ambigüedad
        when(queryService.handle(any(GetCuppingSessionsByUserIdQuery.class))).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/cupping-sessions"))
                .andExpect(status().isOk());
    }
}
