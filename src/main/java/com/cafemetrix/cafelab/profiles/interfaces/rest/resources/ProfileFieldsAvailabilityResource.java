package com.cafemetrix.cafelab.profiles.interfaces.rest.resources;

/**
 * Respuesta del endpoint de disponibilidad de campos del perfil. Cada flag es {@code true} cuando
 * el valor propuesto ya pertenece a otro perfil distinto del usuario que se está editando.
 */
public record ProfileFieldsAvailabilityResource(
        boolean emailTaken, boolean nameTaken, boolean cafeteriaNameTaken) {}
