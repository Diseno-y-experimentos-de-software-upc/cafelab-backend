package com.cafemetrix.cafelab.profiles.domain.model.queries;

/**
 * Pregunta al lado lectura si los valores propuestos para un perfil ya están en uso por otro
 * perfil distinto al identificado por {@code excludingUserId}. Cualquier campo {@code null}/blanco
 * se ignora (no se valida).
 */
public record CheckProfileFieldAvailabilityQuery(
        Long excludingUserId, String email, String name, String cafeteriaName) {}
