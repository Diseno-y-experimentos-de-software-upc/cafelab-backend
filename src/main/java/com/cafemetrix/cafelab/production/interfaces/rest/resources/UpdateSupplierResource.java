package com.cafemetrix.cafelab.production.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.util.List;

public record UpdateSupplierResource(
    @JsonProperty("name")
    @NotBlank(message = "Name es requerido")
    @Size(min = 2, max = 100, message = "Name debe tener entre 2 y 100 caracteres")
    @Pattern(
            regexp = "^(?!\\d+$)(?![,.;]+$)[A-Za-zÁÉÍÓÚáéíóúÑñÜü\\s]+$",
            message = "Name solo puede contener letras, espacios y tildes."
    )
    String name,

    @JsonProperty("email")
    @NotBlank(message = "Email es requerido")
    @Email(message = "Email debe tener un formato válido")
    @Size(max = 100, message = "Email no puede tener más de 100 caracteres")
    @Pattern(
            regexp = "^(?!-)[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Email debe tener un formato válido"
    )
    String email,

    @JsonProperty("phone")
    @NotNull(message = "Phone es requerido")
    @Positive(message = "Phone debe ser positivo")
    @Digits(integer = 15, fraction = 0, message = "Phone debe contener solo números enteros y máximo 15 dígitos")
    @Min(value = 1000000, message = "Phone debe tener al menos 7 dígitos")
    Long phone,

    @JsonProperty("location")
    @NotBlank(message = "Location es requerido")
    @Size(min = 2, max = 200, message = "Location debe tener entre 2 y 200 caracteres")
    @Pattern(
            regexp = "^(?!\\d+$)(?![,.;]+$)[A-Za-zÁÉÍÓÚáéíóúÑñÜü0-9\\s,.;]+$",
            message = "Location solo puede contener letras, números, espacios, tildes, comas, puntos y punto y coma; no puede ser solo números ni solo signos de puntuación"
    )
    String location,

    @JsonProperty("specialties")
    @Size(max = 4, message = "No se pueden tener más de 4 especialidades")
    List<
            @NotBlank(message = "La especialidad no puede estar vacía")
            @Size(min = 2, max = 100, message = "Cada especialidad debe tener entre 2 y 100 caracteres")
            @Pattern(
                    regexp = "^(?!\\d+$)(?![,.;]+$)[A-Za-zÁÉÍÓÚáéíóúÑñÜü\\s]+$",
                    message = "Cada especialidad solo puede contener letras, espacios y tildes; no puede ser solo números ni solo signos de puntuación"
            )
                    String
            > specialties
) {}
