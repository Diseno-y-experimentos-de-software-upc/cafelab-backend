package com.cafemetrix.cafelab.core.unit.tests;

import com.cafemetrix.cafelab.calibrations.domain.model.aggregates.GrindCalibration;
import com.cafemetrix.cafelab.calibrations.domain.model.commands.CreateGrindCalibrationCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("US08 - Calibración de Molienda")
class US08_GrindCalibrationTest {

    @Test
    @DisplayName("registra configuracion de molienda y normaliza campos opcionales vacios")
    void createsGrindCalibration() {
        var calibration = new GrindCalibration(new CreateGrindCalibrationCommand(
                7L, "  V60 receta base  ", "V60", "Comandante C40", "24 clicks",
                650.0, 250.0, 215.0, LocalDate.of(2026, 5, 12),
                "  buen flujo  ", "   ", null));

        assertThat(calibration.getUserId()).isEqualTo(7L);
        assertThat(calibration.getName()).isEqualTo("V60 receta base");
        assertThat(calibration.getGrindNumber()).isEqualTo("24 clicks");
        assertThat(calibration.getAperture()).isEqualTo(650.0);
        assertThat(calibration.getComments()).isEqualTo("buen flujo");
        assertThat(calibration.getNotes()).isNull();
    }
}
