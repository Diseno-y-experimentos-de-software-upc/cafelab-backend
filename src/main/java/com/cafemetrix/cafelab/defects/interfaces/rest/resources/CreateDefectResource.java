package com.cafemetrix.cafelab.defects.interfaces.rest.resources;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo POST/PUT: sin userId (lo fija el JWT). Validación Bean para respuestas 400 con {@code errors[]}.
 */
public record CreateDefectResource(
        @NotBlank(message = "Indique el nombre del café.")
        @Size(max = 255, message = "El nombre del café no puede superar 255 caracteres.")
        @Pattern(
                regexp = "^(?=.*\\p{L})[\\p{L} ]+$",
                message = "DEFECT_BC.FORM.ERRORS.COFFEE_NAME_LETTERS_ONLY")
        String coffeeDisplayName,

        @Size(max = 255, message = "La región no puede superar 255 caracteres.")
        String coffeeRegion,

        @NotBlank(message = "Indique la variedad del café.")
        @Size(max = 255, message = "La variedad no puede superar 255 caracteres.")
        String coffeeVariety,

        @PositiveOrZero(message = "El peso total del café no puede ser negativo.")
        Double coffeeTotalWeight,

        @NotBlank(message = "Indique el nombre del defecto.")
        @Size(max = 255, message = "El nombre del defecto no puede superar 255 caracteres.")
        String name,

        @NotBlank(message = "Indique el tipo de defecto.")
        @Size(max = 255, message = "El tipo de defecto no puede superar 255 caracteres.")
        String defectType,

        @NotNull(message = "Indique el peso del defecto.")
        @Positive(message = "El peso del defecto debe ser mayor que cero.")
        Double defectWeight,

        @NotNull(message = "Indique el porcentaje.")
        @DecimalMin(value = "0.0", inclusive = true, message = "El porcentaje debe estar entre 0 y 100.")
        @DecimalMax(value = "100.0", inclusive = true, message = "El porcentaje debe estar entre 0 y 100.")
        Double percentage,

        @NotBlank(message = "Indique la causa probable.")
        String probableCause,

        @NotBlank(message = "Indique la solución sugerida.")
        String suggestedSolution) {}
