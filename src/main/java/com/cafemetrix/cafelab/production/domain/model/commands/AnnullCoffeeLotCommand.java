package com.cafemetrix.cafelab.production.domain.model.commands;

/**
 * Marca un lote (y todas sus versiones) como {@code anulado} sin borrarlo (queda para auditoría).
 */
public record AnnullCoffeeLotCommand(Long coffeeLotId, String reason) {
    public AnnullCoffeeLotCommand {
        if (coffeeLotId == null || coffeeLotId <= 0) {
            throw new IllegalArgumentException("CoffeeLotId es requerido y debe ser positivo");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("El motivo es obligatorio");
        }
    }
}
