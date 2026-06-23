package com.cafemetrix.cafelab.iam.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignUpResource(
        @NotBlank(message = "El correo es obligatorio")
                @Email(message = "Correo inválido")
                String email,
        @NotBlank(message = "La contraseña es obligatoria")
                @Size(min = 8, max = 120, message = "La contraseña debe tener entre 8 y 120 caracteres")
                @Pattern(
                        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_\\-#])[A-Za-z\\d@$!%*?&_\\-#]{8,}$",
                        message = "La contraseña debe incluir al menos una mayúscula, una minúscula, un número y un carácter especial (@$!%*?&_-#)")
                String password,
        String role) {
    public SignUpResource(String email, String password) {
        this(email, password, null);
    }
}
