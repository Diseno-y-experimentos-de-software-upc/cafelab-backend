package com.cafemetrix.cafelab.production.domain.model.commands;

public record UpdateCoffeeLotStockCommand(Long coffeeLotId, Double weight) {
    public UpdateCoffeeLotStockCommand {
        if (coffeeLotId == null || coffeeLotId <= 0) {
            throw new IllegalArgumentException("CoffeeLotId es requerido y debe ser positivo");
        }
        if (weight == null || weight < 0) {
            throw new IllegalArgumentException("Weight es requerido y no puede ser negativo");
        }
    }
}
