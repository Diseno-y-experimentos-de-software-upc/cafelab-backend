package com.cafemetrix.cafelab.profiles.interfaces.rest.resources;

/**
 * Cuerpo del 409 cuando el update intenta usar un valor único ya tomado por otro perfil.
 * {@code field} sigue los identificadores que el frontend usa para mostrar el error
 * (e.g. {@code "email"}, {@code "name"}, {@code "cafeteriaName"}).
 */
public record ProfileFieldInUseResource(String field, String message) {}
