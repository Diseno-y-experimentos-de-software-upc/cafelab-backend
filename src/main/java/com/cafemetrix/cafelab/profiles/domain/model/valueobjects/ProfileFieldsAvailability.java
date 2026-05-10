package com.cafemetrix.cafelab.profiles.domain.model.valueobjects;

/**
 * Resultado de validar disponibilidad para los tres campos únicos del perfil. Cada flag es
 * {@code true} cuando el valor propuesto ya está en uso por otro perfil distinto al actual; si el
 * campo no se evaluó, queda en {@code false}.
 */
public record ProfileFieldsAvailability(
        boolean emailTaken, boolean nameTaken, boolean cafeteriaNameTaken) {

    public boolean anyTaken() {
        return emailTaken || nameTaken || cafeteriaNameTaken;
    }
}
