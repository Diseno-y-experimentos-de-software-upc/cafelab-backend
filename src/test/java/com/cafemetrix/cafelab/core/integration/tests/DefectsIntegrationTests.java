package com.cafemetrix.cafelab.core.integration.tests;

import com.cafemetrix.cafelab.defects.domain.model.aggregates.Defect;
import com.cafemetrix.cafelab.defects.domain.model.commands.CreateDefectCommand;
import com.cafemetrix.cafelab.defects.domain.model.queries.GetDefectsByUserIdQuery;
import com.cafemetrix.cafelab.defects.domain.services.DefectCommandService;
import com.cafemetrix.cafelab.defects.domain.services.DefectQueryService;
import com.cafemetrix.cafelab.defects.interfaces.rest.DefectsController;
import com.cafemetrix.cafelab.defects.interfaces.rest.resources.CreateDefectResource;
import com.cafemetrix.cafelab.iam.infrastructure.authorization.sfs.support.CurrentProfileIdResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

// US03: Biblioteca de Defectos de Tueste

public class DefectsIntegrationTests {
    private DefectsController defectsController;
    private DefectCommandService defectCommandService;
    private DefectQueryService defectQueryService;

    // Facades
    CurrentProfileIdResolver currentProfileIdResolver;

    @BeforeEach
    void setUp() {
        defectCommandService = Mockito.mock(DefectCommandService.class);
        defectQueryService = Mockito.mock(DefectQueryService.class);

        // Facades
        currentProfileIdResolver = Mockito.mock(CurrentProfileIdResolver.class);

        defectsController = new DefectsController(defectCommandService, defectQueryService, currentProfileIdResolver);
    }

    @Test
    void createDefect_ReturnsCreated_WhenValidInput() {
        var resource = new CreateDefectResource(
                "Café Supremo",
                "Colombia",
                "Arabica",
                100.0,
                "Defecto de tueste",
                "Tueste demasiado oscuro",
                5.0,
                5.0,
                "Probable causa del defecto",
                "Sugerencia de solución"
        );

        Defect mockDefect = Mockito.mock(Defect.class);

        Mockito.when(mockDefect.getId()).thenReturn(1L);
        Mockito.when(mockDefect.getName()).thenReturn("Café Supremo");
        Mockito.when(mockDefect.getCoffeeRegion()).thenReturn("Colombia");
        Mockito.when(mockDefect.getCoffeeVariety()).thenReturn("Arabica");
        Mockito.when(mockDefect.getCoffeeTotalWeight()).thenReturn(100.0);
        Mockito.when(mockDefect.getDefectType()).thenReturn("Defecto de tueste");
        Mockito.when(mockDefect.getDefectWeight()).thenReturn(5.0);
        Mockito.when(mockDefect.getPercentage()).thenReturn(5.0);
        Mockito.when(mockDefect.getProbableCause()).thenReturn("Probable causa del defecto");
        Mockito.when(mockDefect.getSuggestedSolution()).thenReturn("Sugerencia de solución");

        Mockito.when(currentProfileIdResolver.resolveProfileId())
                .thenReturn(Optional.of(1L));

        Mockito.when(defectCommandService.handle(Mockito.any(CreateDefectCommand.class)))
                .thenReturn(Optional.of(mockDefect));

        var response = defectsController.createDefect(resource);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    void getAllDefectsForCurrentProfile_ReturnsOK_WhenAuthenticated() {
        Defect mockDefect = Mockito.mock(Defect.class);

        Mockito.when(mockDefect.getId()).thenReturn(1L);
        Mockito.when(mockDefect.getName()).thenReturn("Café Supremo");
        Mockito.when(mockDefect.getCoffeeRegion()).thenReturn("Colombia");
        Mockito.when(mockDefect.getCoffeeVariety()).thenReturn("Arabica");
        Mockito.when(mockDefect.getCoffeeTotalWeight()).thenReturn(100.0);
        Mockito.when(mockDefect.getDefectType()).thenReturn("Defecto de tueste");
        Mockito.when(mockDefect.getDefectWeight()).thenReturn(5.0);
        Mockito.when(mockDefect.getPercentage()).thenReturn(5.0);
        Mockito.when(mockDefect.getProbableCause()).thenReturn("Probable causa del defecto");
        Mockito.when(mockDefect.getSuggestedSolution()).thenReturn("Sugerencia de solución");

        Mockito.when(currentProfileIdResolver.resolveProfileId())
                .thenReturn(Optional.of(1L));

        Mockito.when(defectQueryService.handle(Mockito.any(GetDefectsByUserIdQuery.class)))
                .thenReturn(List.of(mockDefect));

        var response = defectsController.getDefectsForCurrentProfile();

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }

}
