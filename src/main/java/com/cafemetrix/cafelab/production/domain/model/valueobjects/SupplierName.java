package com.cafemetrix.cafelab.production.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public record SupplierName(String value) {
    public SupplierName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El nombre del proveedor no puede ser nulo o vacío");
        }

        var trimmedValue = value.trim();

        if (trimmedValue.length() < 2 || trimmedValue.length() > 100) {
            throw new IllegalArgumentException("El nombre del proveedor debe tener entre 2 y 100 caracteres");
        }

        if (trimmedValue.matches("^\\d+$")) {
            throw new IllegalArgumentException("El nombre del proveedor no puede contener solo números");
        }

        if (trimmedValue.matches("^[,.;]+$")) {
            throw new IllegalArgumentException("El nombre del proveedor solo puede contener letras, espacios y tildes");
        }

        if (!trimmedValue.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñÜü\\s]+$")) {
            throw new IllegalArgumentException("El nombre del proveedor solo puede contener letras, espacios y tildes");
        }

        value = trimmedValue;
    }

    public SupplierName() {
        this(null);
    }
}
