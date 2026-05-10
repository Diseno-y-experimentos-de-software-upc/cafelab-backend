package com.cafemetrix.cafelab.production.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

import java.util.regex.Pattern;

@Embeddable
public record SupplierEmail(String value) {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^(?!-)[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public SupplierEmail {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El email del proveedor no puede ser nulo o vacío");
        }

        var normalizedValue = value.trim().toLowerCase();

        if (normalizedValue.length() > 100) {
            throw new IllegalArgumentException("El email del proveedor no puede tener más de 100 caracteres");
        }

        if (!EMAIL_PATTERN.matcher(normalizedValue).matches()) {
            throw new IllegalArgumentException("El email del proveedor debe tener un formato válido");
        }

        value = normalizedValue;
    }

    public SupplierEmail() {
        this(null);
    }
}
