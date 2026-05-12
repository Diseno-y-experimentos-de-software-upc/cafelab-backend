package com.cafemetrix.cafelab.core.unit.tests;

import com.cafemetrix.cafelab.defects.domain.model.aggregates.Defect;
import com.cafemetrix.cafelab.defects.domain.model.commands.CreateDefectCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("US04 - Defectos de Tueste")
class US04_RoastDefectTest {

    @Test
    @DisplayName("registra defecto con causa probable y solucion sugerida")
    void createsRoastDefect() {
        var defect = new Defect(new CreateDefectCommand(
                7L, "Lote Norte 2026", "Jaen", "Caturra", 500.0,
                "Scorching", "Roast", 15.0, 3.0,
                "Carga inicial demasiado alta", "Reducir temperatura inicial"));

        assertThat(defect.getUserId()).isEqualTo(7L);
        assertThat(defect.getName()).isEqualTo("Scorching");
        assertThat(defect.getPercentage()).isEqualTo(3.0);
        assertThat(defect.getProbableCause()).contains("Carga inicial");
    }
}
