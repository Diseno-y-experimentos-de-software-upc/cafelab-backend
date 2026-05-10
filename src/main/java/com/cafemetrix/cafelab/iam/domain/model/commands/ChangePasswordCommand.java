package com.cafemetrix.cafelab.iam.domain.model.commands;

/**
 * Cambio de contraseña autenticado: el usuario debe demostrar que conoce la contraseña actual antes
 * de que se acepte la nueva. {@code email} se resuelve del JWT en la capa de interfaces.
 */
public record ChangePasswordCommand(String email, String currentPassword, String newPassword) {}
