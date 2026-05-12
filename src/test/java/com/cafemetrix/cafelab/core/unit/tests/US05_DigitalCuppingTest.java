package com.cafemetrix.cafelab.core.unit.tests;

import com.cafemetrix.cafelab.cuppingsessions.domain.model.aggregates.CuppingSession;
import com.cafemetrix.cafelab.cuppingsessions.domain.model.commands.CreateCuppingSessionCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("US05 - Cata Digital")
class US05_DigitalCuppingTest {

    @Test
    @DisplayName("registra una sesion de cupping con resultados sensoriales")
    void createsCuppingSessionWithSensoryResults() {
        var session = new CuppingSession(new CreateCuppingSessionCommand(
                7L, "  Cupping Geisha  ", "  Chiriqui  ", "Geisha",
                "washed", LocalDate.of(2026, 5, 12), true,
                "{\"aroma\":8.5,\"aftertaste\":8.0}", "  tostado medio  "));

        assertThat(session.getName()).isEqualTo("Cupping Geisha");
        assertThat(session.getOrigin()).isEqualTo("Chiriqui");
        assertThat(session.isFavorite()).isTrue();
        assertThat(session.getResultsJson()).contains("\"aroma\":8.5");
        assertThat(session.getRoastStyleNotes()).isEqualTo("tostado medio");
    }
}
