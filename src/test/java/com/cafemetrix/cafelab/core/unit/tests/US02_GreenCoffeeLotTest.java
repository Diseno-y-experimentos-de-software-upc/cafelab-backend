package com.cafemetrix.cafelab.core.unit.tests;

import com.cafemetrix.cafelab.production.domain.model.aggregates.CoffeeLot;
import com.cafemetrix.cafelab.production.domain.model.commands.CreateCoffeeLotCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("US02 - Gestión de Lotes de Café Verde")
class US02_GreenCoffeeLotTest {

    @Test
    @DisplayName("registra un lote verde con trazabilidad de origen, peso y certificaciones")
    void createsGreenCoffeeLotForTraceability() {
        var lot = new CoffeeLot(new CreateCoffeeLotCommand(
                7L, 3L, "Lote Norte 2026", "Arábica", "Lavado",
                1850, 150.5, "Jaen", "green", List.of("Organic", "Fair Trade")));

        assertThat(lot.getUserId()).isEqualTo(7L);
        assertThat(lot.getSupplierId()).isEqualTo(3L);
        assertThat(lot.getLotName()).isEqualTo("Lote Norte 2026");
        assertThat(lot.getWeight()).isEqualTo(150.5);
        assertThat(lot.getStatus()).isEqualTo("green");
        assertThat(lot.getCertifications()).containsExactly("Organic", "Fair Trade");
    }

    @Test
    @DisplayName("rechaza estados fuera de green/roasted")
    void rejectsUnknownLotStatus() {
        assertThatThrownBy(() -> new CoffeeLot(new CreateCoffeeLotCommand(
                7L, 3L, "Lote Norte 2026", "Arábica", "Lavado",
                1850, 150.5, "Jaen", "sold", List.of())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("estado");
    }
}
