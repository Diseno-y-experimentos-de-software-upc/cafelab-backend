package com.cafemetrix.cafelab.production.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateRoastProfileResource(
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede superar 50 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ ]+$", message = "El nombre no puede contener números ni caracteres especiales")
    String name,

    @NotBlank(message = "El tipo es obligatorio")
    @Size(max = 20, message = "El tipo no puede superar 20 caracteres")
    String type,

    @NotNull(message = "La duración es obligatoria")
    @Min(value = 1, message = "La duración mínima es 1 minuto")
    @Max(value = 15, message = "La duración no puede superar 15 minutos")
    Integer duration,

    @NotNull(message = "La temperatura inicial es obligatoria")
    @DecimalMin(value = "1", message = "La temperatura inicial debe ser al menos 1 °C")
    @DecimalMax(value = "240", message = "La temperatura inicial no puede superar 240 °C")
    @Digits(integer = 3, fraction = 0, message = "La temperatura inicial debe ser un número entero")
    Double tempStart,

    @NotNull(message = "La temperatura final es obligatoria")
    @DecimalMin(value = "1", message = "La temperatura final debe ser al menos 1 °C")
    @DecimalMax(value = "240", message = "La temperatura final no puede superar 240 °C")
    @Digits(integer = 3, fraction = 0, message = "La temperatura final debe ser un número entero")
    Double tempEnd,

    @JsonProperty("lot")
    @NotNull(message = "Debe vincular un lote de café")
    @Positive(message = "Lote inválido")
    Long lot,

    @NotNull(message = "Debe indicar si es favorito")
    Boolean isFavorite
) {}
