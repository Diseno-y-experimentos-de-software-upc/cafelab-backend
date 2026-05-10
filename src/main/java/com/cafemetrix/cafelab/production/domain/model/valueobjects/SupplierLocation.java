package com.cafemetrix.cafelab.production.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public record SupplierLocation(String value) {
    public SupplierLocation {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("La ubicación del proveedor no puede ser nula o vacía");
        }

        var trimmedValue = value.trim();

        if (trimmedValue.length() < 2 || trimmedValue.length() > 200) {
            throw new IllegalArgumentException("La ubicación del proveedor debe tener entre 2 y 200 caracteres");
        }

        if (trimmedValue.matches("^\\d+$")) {
            throw new IllegalArgumentException("La ubicación del proveedor no puede contener solo números");
        }

        if (trimmedValue.matches("^[,.;]+$")) {
            throw new IllegalArgumentException("La ubicación del proveedor no puede contener solo comas, puntos o punto y coma");
        }

        if (!trimmedValue.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñÜü0-9\\s,.;]+$")) {
            throw new IllegalArgumentException("La ubicación del proveedor solo puede contener letras, números, espacios, tildes, comas, puntos y punto y coma");
        }

        value = trimmedValue;
    }

    public SupplierLocation() {
        this(null);
    }
}
