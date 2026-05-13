package com.cafemetrix.cafelab.management.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public record FinalProduct(String value) {
    public FinalProduct {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El producto final no puede ser nulo o vacío");
        }
        if (value.length() > 100) {
            throw new IllegalArgumentException("El producto final no puede tener más de 100 caracteres");
        }
    }

    public FinalProduct() {
        this(null);
    }
}
