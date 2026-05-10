package com.cafemetrix.cafelab.iam.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Petición de sign-up. Mantenemos {@code 6..120} en {@code password} para alinear con sign-in y
 * con el cambio de contraseña.
 */
public record SignUpResource(
        @NotBlank(message = "El correo es obligatorio")
                @Email(message = "Correo inválido")
                String email,
        @NotBlank(message = "La contraseña es obligatoria")
                @Size(min = 6, max = 120, message = "La contraseña debe tener entre 6 y 120 caracteres")
                String password,
        String role) {
    public SignUpResource(String email, String password) {
        this(email, password, null);
    }
}
