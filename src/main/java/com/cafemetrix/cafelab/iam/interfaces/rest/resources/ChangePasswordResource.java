package com.cafemetrix.cafelab.iam.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChangePasswordResource(
        @NotBlank(message = "La contraseña actual es obligatoria") String currentPassword,
        @NotBlank(message = "La nueva contraseña es obligatoria")
                @Size(min = 8, max = 120, message = "La nueva contraseña debe tener entre 8 y 120 caracteres")
                @Pattern(
                        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_\\-#])[A-Za-z\\d@$!%*?&_\\-#]{8,}$",
                        message = "La nueva contraseña debe incluir al menos una mayúscula, una minúscula, un número y un carácter especial (@$!%*?&_-#)")
                String newPassword) {}
