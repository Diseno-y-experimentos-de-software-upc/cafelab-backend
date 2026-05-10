package com.cafemetrix.cafelab.iam.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo de la petición de cambio de contraseña. {@code currentPassword} es obligatorio: el
 * frontend exige al usuario reescribirla como prueba de identidad antes de aceptar la nueva
 * contraseña.
 */
public record ChangePasswordResource(
        @NotBlank(message = "La contraseña actual es obligatoria") String currentPassword,
        @NotBlank(message = "La nueva contraseña es obligatoria")
                @Size(min = 6, max = 120, message = "La nueva contraseña debe tener entre 6 y 120 caracteres")
                String newPassword) {}
