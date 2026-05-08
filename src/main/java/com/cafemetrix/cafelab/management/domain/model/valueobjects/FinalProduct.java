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

        if (!value.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9 .,-]+$")) {
            throw new IllegalArgumentException("El producto final solo puede contener letras, números y espacios");
        }

    }

    public FinalProduct() {
        this(null);
    }
}
