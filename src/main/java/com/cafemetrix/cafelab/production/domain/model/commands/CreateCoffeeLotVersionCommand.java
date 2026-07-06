package com.cafemetrix.cafelab.production.domain.model.commands;

import java.util.List;

public record CreateCoffeeLotVersionCommand(
    Long coffeeLotId,
    String lotName,
    String coffeeType,
    String processingMethod,
    Integer altitude,
    String origin,
    String status,
    List<String> certifications
) {
    public CreateCoffeeLotVersionCommand {
        if (coffeeLotId == null || coffeeLotId <= 0) {
            throw new IllegalArgumentException("CoffeeLotId es requerido y debe ser positivo");
        }
        if (lotName == null || lotName.isBlank()) {
            throw new IllegalArgumentException("LotName es requerido");
        }
        if (coffeeType == null || coffeeType.isBlank()) {
            throw new IllegalArgumentException("CoffeeType es requerido");
        }
        if (processingMethod == null || processingMethod.isBlank()) {
            throw new IllegalArgumentException("ProcessingMethod es requerido");
        }
        if (altitude == null || altitude < 0) {
            throw new IllegalArgumentException("Altitude es requerido y no puede ser negativo");
        }
        if (origin == null || origin.isBlank()) {
            throw new IllegalArgumentException("Origin es requerido");
        }
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Status es requerido");
        }
    }
}
