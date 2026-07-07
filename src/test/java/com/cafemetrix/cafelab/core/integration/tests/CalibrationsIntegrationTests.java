package com.cafemetrix.cafelab.core.integration.tests;


import com.cafemetrix.cafelab.calibrations.domain.model.aggregates.GrindCalibration;
import com.cafemetrix.cafelab.calibrations.domain.model.commands.CreateGrindCalibrationCommand;
import com.cafemetrix.cafelab.calibrations.domain.model.queries.GetGrindCalibrationsByUserIdQuery;
import com.cafemetrix.cafelab.calibrations.domain.services.GrindCalibrationCommandService;
import com.cafemetrix.cafelab.calibrations.domain.services.GrindCalibrationQueryService;
import com.cafemetrix.cafelab.calibrations.interfaces.rest.CalibrationsController;
import com.cafemetrix.cafelab.calibrations.interfaces.rest.resources.CreateGrindCalibrationResource;
import com.cafemetrix.cafelab.iam.infrastructure.authorization.sfs.support.CurrentProfileIdResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// US08: Calibración de Molienda
public class CalibrationsIntegrationTests {
    private GrindCalibrationCommandService grindCalibrationCommandService;
    private GrindCalibrationQueryService grindCalibrationQueryService;
    private CalibrationsController calibrationsController;

    // Facades
    private CurrentProfileIdResolver currentProfileIdResolver;

    @BeforeEach
    void setUp() {
        grindCalibrationCommandService = Mockito.mock(GrindCalibrationCommandService.class);
        grindCalibrationQueryService = Mockito.mock(GrindCalibrationQueryService.class);

        // Facades

        currentProfileIdResolver = Mockito.mock(CurrentProfileIdResolver.class);

        calibrationsController = new CalibrationsController(grindCalibrationCommandService, grindCalibrationQueryService, currentProfileIdResolver);
    }

    @Test
    void createGrindCalibration_ReturnsCreated_WhenValidInput() {
        var dateCalibration = LocalDate.now();

        var resource = new CreateGrindCalibrationResource(
                "Calibración de molienda para espresso",
                "Espresso",
                "Máquina de espresso XYZ",
                "Grind #1",
                0.5,
                30.0,
                30.0,
                dateCalibration,
                "Calibración inicial para molienda fina",
                "Esta calibración se realizó con café de origen Colombia, tueste medio",
                "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA"
        );

        GrindCalibration mockGrindCalibration = Mockito.mock(GrindCalibration.class);

        Mockito.when(mockGrindCalibration.getId()).thenReturn(1L);
        Mockito.when(mockGrindCalibration.getName()).thenReturn("Calibración de molienda para espresso");
        Mockito.when(mockGrindCalibration.getMethod()).thenReturn("Espresso");
        Mockito.when(mockGrindCalibration.getEquipment()).thenReturn("Máquina de espresso XYZ");
        Mockito.when(mockGrindCalibration.getGrindNumber()).thenReturn("Grind #1");
        Mockito.when(mockGrindCalibration.getAperture()).thenReturn(0.5);
        Mockito.when(mockGrindCalibration.getCupVolume()).thenReturn(30.0);
        Mockito.when(mockGrindCalibration.getFinalVolume()).thenReturn(30.0);
        Mockito.when(mockGrindCalibration.getCalibrationDate()).thenReturn(dateCalibration);
        Mockito.when(mockGrindCalibration.getComments()).thenReturn("Calibración inicial para molienda fina");
        Mockito.when(mockGrindCalibration.getNotes()).thenReturn("Esta calibración se realizó con café de origen Colombia, tueste medio");
        Mockito.when(mockGrindCalibration.getSampleImage()).thenReturn("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA");

        Mockito.when(currentProfileIdResolver.resolveProfileId())
                .thenReturn(Optional.of(1L));

        Mockito.when(grindCalibrationCommandService.handle(Mockito.any(CreateGrindCalibrationCommand.class)))
                .thenReturn(Optional.of(mockGrindCalibration));

        var response = calibrationsController.create(resource);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    void listGrindCalibrationsForCurrentProfile_ReturnsOk_WhenAuthenticated() {
        var dateCalibration = LocalDate.now();

        GrindCalibration mockGrindCalibration = Mockito.mock(GrindCalibration.class);

        Mockito.when(mockGrindCalibration.getId()).thenReturn(1L);
        Mockito.when(mockGrindCalibration.getName()).thenReturn("Calibración de molienda para espresso");
        Mockito.when(mockGrindCalibration.getMethod()).thenReturn("Espresso");
        Mockito.when(mockGrindCalibration.getEquipment()).thenReturn("Máquina de espresso XYZ");
        Mockito.when(mockGrindCalibration.getGrindNumber()).thenReturn("Grind #1");
        Mockito.when(mockGrindCalibration.getAperture()).thenReturn(0.5);
        Mockito.when(mockGrindCalibration.getCupVolume()).thenReturn(30.0);
        Mockito.when(mockGrindCalibration.getFinalVolume()).thenReturn(30.0);
        Mockito.when(mockGrindCalibration.getCalibrationDate()).thenReturn(dateCalibration);
        Mockito.when(mockGrindCalibration.getComments()).thenReturn("Calibración inicial para molienda fina");
        Mockito.when(mockGrindCalibration.getNotes()).thenReturn("Esta calibración se realizó con café de origen Colombia, tueste medio");
        Mockito.when(mockGrindCalibration.getSampleImage()).thenReturn("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA");

        Mockito.when(currentProfileIdResolver.resolveProfileId())
                .thenReturn(Optional.of(1L));

        Mockito.when(grindCalibrationQueryService.handle(Mockito.any(GetGrindCalibrationsByUserIdQuery.class)))
                .thenReturn(List.of(mockGrindCalibration));

        var response = calibrationsController.listForCurrentProfile();

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }
}
