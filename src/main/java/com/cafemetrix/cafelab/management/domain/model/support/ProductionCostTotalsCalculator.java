package com.cafemetrix.cafelab.management.domain.model.support;

/** Totales alineados con el cálculo del frontend de gestión de costos. */
public final class ProductionCostTotalsCalculator {

    private ProductionCostTotalsCalculator() {}

    public record Totals(
            double totalDirectCost,
            double totalIndirectCost,
            double totalCost,
            double costPerKg,
            double suggestedPrice,
            double potentialMargin) {}

    public static Totals compute(
            double totalKg,
            double marginPercent,
            double rawMaterialsCost,
            double laborCost,
            double transportCost,
            double storageCost,
            double processingCost,
            double otherIndirectCosts) {
        double totalDirect = rawMaterialsCost + laborCost;
        double totalIndirect = transportCost + storageCost + processingCost + otherIndirectCosts;
        double totalCost = totalDirect + totalIndirect;
        double costPerKg = totalKg > 0 ? totalCost / totalKg : 0;
        double suggestedPrice = costPerKg * (1 + marginPercent / 100.0);
        double potentialMargin =
                suggestedPrice > 0 ? ((suggestedPrice - costPerKg) / suggestedPrice) * 100.0 : 0;
        return new Totals(totalDirect, totalIndirect, totalCost, costPerKg, suggestedPrice, potentialMargin);
    }
}
