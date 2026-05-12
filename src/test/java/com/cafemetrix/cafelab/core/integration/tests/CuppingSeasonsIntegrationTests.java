package com.cafemetrix.cafelab.core.integration.tests;

import com.cafemetrix.cafelab.cuppingsessions.domain.model.aggregates.CuppingSession;
import com.cafemetrix.cafelab.cuppingsessions.domain.model.commands.CreateCuppingSessionCommand;
import com.cafemetrix.cafelab.cuppingsessions.domain.model.queries.GetCuppingSessionByIdForUserQuery;
import com.cafemetrix.cafelab.cuppingsessions.domain.services.CuppingSessionCommandService;
import com.cafemetrix.cafelab.cuppingsessions.domain.services.CuppingSessionQueryService;
import com.cafemetrix.cafelab.cuppingsessions.interfaces.rest.CuppingSessionsController;
import com.cafemetrix.cafelab.cuppingsessions.interfaces.rest.resources.CreateCuppingSessionResource;
import com.cafemetrix.cafelab.iam.infrastructure.authorization.sfs.support.CurrentProfileIdResolver;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.Optional;

// US05: Cata Digital Estructurada
// US06: Historial de Catas
@Slf4j
public class CuppingSeasonsIntegrationTests {
    private CuppingSessionCommandService cuppingSessionCommandService;
    private CuppingSessionQueryService cuppingSessionQueryService;
    private CuppingSessionsController cuppingSessionsController;

    // Facades
    private CurrentProfileIdResolver currentProfileIdResolver;

    @BeforeEach
    void setUp() {
        cuppingSessionCommandService = Mockito.mock(CuppingSessionCommandService.class);
        cuppingSessionQueryService = Mockito.mock(CuppingSessionQueryService.class);

        // Facades
        currentProfileIdResolver = Mockito.mock(CurrentProfileIdResolver.class);

        cuppingSessionsController = new CuppingSessionsController(cuppingSessionCommandService, cuppingSessionQueryService, currentProfileIdResolver);

    }

    // US05: Cata Digital Estructurada
    @Test
    void createCuppingSession_ReturnsCreated_WhenValidInput() {
        var dateCreation = LocalDate.now();

        var resource = new CreateCuppingSessionResource(
                "Cata de café de Colombia",
                "Colombia",
                "Arabica",
                "Washing",
                dateCreation,
                true,
                "Aroma y sabor frutales, acidez brillante",
                "Sabor dulce y cuerpo medio, con notas de frutas cítricas y un final limpio"
        );

        CuppingSession mockCuppingSession = Mockito.mock(CuppingSession.class);

        Mockito.when(mockCuppingSession.getId()).thenReturn(1L);
        Mockito.when(mockCuppingSession.getName()).thenReturn("Cata de café de Colombia");
        Mockito.when(mockCuppingSession.getOrigin()).thenReturn("Colombia");
        Mockito.when(mockCuppingSession.getVariety()).thenReturn("Arabica");
        Mockito.when(mockCuppingSession.getProcessing()).thenReturn("Washing");
        Mockito.when(mockCuppingSession.getSessionDate()).thenReturn(dateCreation);
        Mockito.when(mockCuppingSession.isFavorite()).thenReturn(true);
        Mockito.when(mockCuppingSession.getResultsJson()).thenReturn("Aroma y sabor frutales, acidez brillante");
        Mockito.when(mockCuppingSession.getRoastStyleNotes()).thenReturn("Sabor dulce y cuerpo medio, con notas de frutas cítricas y un final limpio");

        Mockito.when(currentProfileIdResolver.resolveProfileId())
                .thenReturn(Optional.of(1L));

        Mockito.when(cuppingSessionCommandService.handle(Mockito.any(CreateCuppingSessionCommand.class)))
                .thenReturn(Optional.of(mockCuppingSession));

        var response = cuppingSessionsController.create(resource);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }

    // US06: Historial de Catas
    @Test
    void listCuppingSessionsForCurrentProfile_ReturnsOk_WhenAuthenticated() {
        var dateCreation = LocalDate.now();

        CuppingSession mockCuppingSession = Mockito.mock(CuppingSession.class);

        Mockito.when(mockCuppingSession.getId()).thenReturn(1L);
        Mockito.when(mockCuppingSession.getName()).thenReturn("Cata de café de Colombia");
        Mockito.when(mockCuppingSession.getOrigin()).thenReturn("Colombia");
        Mockito.when(mockCuppingSession.getVariety()).thenReturn("Arabica");
        Mockito.when(mockCuppingSession.getProcessing()).thenReturn("Washing");
        Mockito.when(mockCuppingSession.getSessionDate()).thenReturn(dateCreation);
        Mockito.when(mockCuppingSession.isFavorite()).thenReturn(true);
        Mockito.when(mockCuppingSession.getResultsJson()).thenReturn("Aroma y sabor frutales, acidez brillante");
        Mockito.when(mockCuppingSession.getRoastStyleNotes()).thenReturn("Sabor dulce y cuerpo medio, con notas de frutas cítricas y un final limpio");

        Mockito.when(currentProfileIdResolver.resolveProfileId())
                .thenReturn(Optional.of(1L));

        Mockito.when(cuppingSessionQueryService.handle(Mockito.any(GetCuppingSessionByIdForUserQuery.class)))
                .thenReturn(Optional.of(mockCuppingSession));

        var response = cuppingSessionsController.list();

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }
}
