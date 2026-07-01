package com.cafemetrix.cafelab.management.domain.model.commands;

import java.time.LocalDateTime;

public record CreateInventoryEntryCommand(
    Long userId,
    Long coffeeLotId,
    Double quantityUsed,
    LocalDateTime dateUsed,
    String finalProduct,
    String consumptionReason,
    String usageNotes
) {
    public CreateInventoryEntryCommand {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("userId es requerido y debe ser positivo");
        }
        if (coffeeLotId == null || coffeeLotId <= 0) throw new IllegalArgumentException("CoffeeLotId es requerido y debe ser positivo");
        if (quantityUsed == null || quantityUsed <= 0) throw new IllegalArgumentException("QuantityUsed es requerido y debe ser positivo");
        if (dateUsed == null) throw new IllegalArgumentException("DateUsed es requerido");
        if (consumptionReason == null || consumptionReason.isBlank()) throw new IllegalArgumentException("ConsumptionReason es requerido");
        consumptionReason = normalizeConsumptionReason(consumptionReason);
        if (!isAllowedConsumptionReason(consumptionReason)) throw new IllegalArgumentException("ConsumptionReason debe ser bar, retail, samples u other");
        usageNotes = normalizeUsageNotes(usageNotes);
        finalProduct = normalizeFinalProduct(finalProduct, consumptionReason);
    }

    private static String normalizeFinalProduct(String finalProduct, String consumptionReason) {
        return finalProduct == null || finalProduct.isBlank() ? consumptionReason : finalProduct.trim();
    }

    private static String normalizeConsumptionReason(String consumptionReason) {
        return consumptionReason.trim().toLowerCase();
    }

    private static String normalizeUsageNotes(String usageNotes) {
        return usageNotes == null || usageNotes.isBlank() ? null : usageNotes.trim();
    }

    private static boolean isAllowedConsumptionReason(String consumptionReason) {
        return consumptionReason.equals("bar")
                || consumptionReason.equals("retail")
                || consumptionReason.equals("samples")
                || consumptionReason.equals("other");
    }
}
