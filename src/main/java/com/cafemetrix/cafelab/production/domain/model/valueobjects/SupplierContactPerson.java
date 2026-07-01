package com.cafemetrix.cafelab.production.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

/** Persona de contacto del proveedor; opcional. Valor null cuando no se informa. */
@Embeddable
public record SupplierContactPerson(String value) {
    public SupplierContactPerson {
        if (value != null && !value.isBlank()) {
            var trimmedValue = value.trim();

            if (trimmedValue.length() < 2 || trimmedValue.length() > 100) {
                throw new IllegalArgumentException("La persona de contacto debe tener entre 2 y 100 caracteres");
            }

            if (!trimmedValue.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñÜü\\s.]+$")) {
                throw new IllegalArgumentException("La persona de contacto solo puede contener letras, espacios, tildes y puntos");
            }

            value = trimmedValue;
        } else {
            value = null;
        }
    }

    public SupplierContactPerson() {
        this(null);
    }
}
