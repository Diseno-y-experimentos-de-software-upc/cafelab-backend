package com.cafemetrix.cafelab.production.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

/** Enlace web del proveedor; opcional. Valor null cuando no se informa. Debe ser URL http(s). */
@Embeddable
public record SupplierWebLink(String value) {
    public SupplierWebLink {
        if (value != null && !value.isBlank()) {
            var trimmedValue = value.trim();

            if (trimmedValue.length() > 200) {
                throw new IllegalArgumentException("El enlace web no puede tener más de 200 caracteres");
            }

            if (!trimmedValue.matches("^https?://[\\w.-]+(:\\d+)?(/\\S*)?$")) {
                throw new IllegalArgumentException("El enlace web debe ser una URL http o https válida");
            }

            value = trimmedValue;
        } else {
            value = null;
        }
    }

    public SupplierWebLink() {
        this(null);
    }
}
