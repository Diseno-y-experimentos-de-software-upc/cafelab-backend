package com.cafemetrix.cafelab.core.unit.tests;

import com.cafemetrix.cafelab.management.domain.model.aggregates.ProductionCostRecord;
import com.cafemetrix.cafelab.management.domain.model.commands.CreateProductionCostRecordCommand;
import com.cafemetrix.cafelab.management.domain.model.support.ProductionCostTotalsCalculator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DisplayName("US13 - Gestión de Costos de Producción")
class US13_ProductionCostTest {

    @Test
    @DisplayName("calcula costos directos, indirectos, costo por kg y precio sugerido")
    void computesProductionCostTotals() {
        var totals = ProductionCostTotalsCalculator.compute(
                50.0, 25.0, 400.0, 100.0, 60.0, 40.0, 80.0, 20.0);

        assertThat(totals.totalDirectCost()).isEqualTo(500.0);
        assertThat(totals.totalIndirectCost()).isEqualTo(200.0);
        assertThat(totals.totalCost()).isEqualTo(700.0);
        assertThat(totals.costPerKg()).isEqualTo(14.0);
        assertThat(totals.suggestedPrice()).isEqualTo(17.5);
        assertThat(totals.potentialMargin()).isCloseTo(20.0, within(0.001));
    }

    @Test
    @DisplayName("registra y anula un costo conservando auditoria")
    void createsAndAnnulsProductionCostRecord() {
        var totals = ProductionCostTotalsCalculator.compute(
                50.0, 25.0, 400.0, 100.0, 60.0, 40.0, 80.0, 20.0);
        
        // Fix: Usar CoffeeType válido ("Arábica")
        var record = new ProductionCostRecord(new CreateProductionCostRecordCommand(
                7L, 44L, "Lote Norte", "Arábica", "PEN", 50.0, 25.0,
                400.0, 100.0, 60.0, 40.0, 80.0, 20.0,
                totals.totalDirectCost(), totals.totalIndirectCost(), totals.totalCost(),
                totals.costPerKg(), totals.suggestedPrice(), totals.potentialMargin()));

        record.annull("duplicado por importacion larga");

        assertThat(record.getStatus()).isEqualTo(ProductionCostRecord.STATUS_ANNULLED);
        assertThat(record.getReason()).hasSizeLessThanOrEqualTo(ProductionCostRecord.REASON_MAX_LENGTH);
        assertThat(record.isAnnulled()).isTrue();
    }
}
