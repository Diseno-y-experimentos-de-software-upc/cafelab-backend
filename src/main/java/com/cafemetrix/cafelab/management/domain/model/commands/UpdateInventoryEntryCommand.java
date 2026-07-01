package com.cafemetrix.cafelab.management.domain.model.commands;

import java.time.LocalDateTime;

public record UpdateInventoryEntryCommand(
    Long inventoryEntryId,
    Long coffeeLotId,
    Double quantityUsed,
    LocalDateTime dateUsed,
    String finalProduct,
    String motivoDeConsumo,
    String notasDeUso
) {
    public UpdateInventoryEntryCommand {
        if (inventoryEntryId == null || inventoryEntryId <= 0) throw new IllegalArgumentException("InventoryEntryId es requerido y debe ser positivo");
        if (coffeeLotId == null || coffeeLotId <= 0) throw new IllegalArgumentException("CoffeeLotId es requerido y debe ser positivo");
        if (quantityUsed == null || quantityUsed <= 0) throw new IllegalArgumentException("QuantityUsed es requerido y debe ser positivo");
        if (dateUsed == null) throw new IllegalArgumentException("DateUsed es requerido");
        if (motivoDeConsumo == null || motivoDeConsumo.isBlank()) throw new IllegalArgumentException("MotivoDeConsumo es requerido");
        motivoDeConsumo = normalizeMotivoDeConsumo(motivoDeConsumo);
        if (!isAllowedMotivoDeConsumo(motivoDeConsumo)) throw new IllegalArgumentException("MotivoDeConsumo debe ser barra, retail, muestras u otro");
        notasDeUso = normalizeNotasDeUso(notasDeUso);
        finalProduct = normalizeFinalProduct(finalProduct, motivoDeConsumo);
    }

    private static String normalizeFinalProduct(String finalProduct, String motivoDeConsumo) {
        return finalProduct == null || finalProduct.isBlank() ? motivoDeConsumo : finalProduct.trim();
    }

    private static String normalizeMotivoDeConsumo(String motivoDeConsumo) {
        return motivoDeConsumo.trim().toLowerCase();
    }

    private static String normalizeNotasDeUso(String notasDeUso) {
        return notasDeUso == null || notasDeUso.isBlank() ? null : notasDeUso.trim();
    }

    private static boolean isAllowedMotivoDeConsumo(String motivoDeConsumo) {
        return motivoDeConsumo.equals("barra")
                || motivoDeConsumo.equals("retail")
                || motivoDeConsumo.equals("muestras")
                || motivoDeConsumo.equals("otro");
    }
}
