package com.cafemetrix.cafelab.iam.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Petición de sign-in. Mantenemos el mismo rango de tamaño que sign-up y change-password (6–120)
 * para que un payload manifiestamente malformado se rechace antes de llegar al hashing.
 */
public record SignInResource(
        @NotBlank(message = "El correo es obligatorio")
                @Email(message = "Correo inválido")
                String email,
        @NotBlank(message = "La contraseña es obligatoria")
                @Size(min = 6, max = 120, message = "La contraseña debe tener entre 6 y 120 caracteres")
                String password) {}
