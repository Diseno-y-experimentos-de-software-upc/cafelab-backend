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
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateCoffeeLotResource(
    @JsonProperty("lot_name")
    @NotBlank(message = "El nombre del lote es obligatorio")
    @Size(max = 100, message = "El nombre del lote no puede superar 100 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9áéíóúÁÉÍÓÚüÜñÑ ]+$", message = "El nombre no puede contener caracteres especiales")
    String lot_name,

    @JsonProperty("coffee_type")
    @NotBlank(message = "El tipo de café es obligatorio")
    @Size(max = 50, message = "El tipo de café no puede superar 50 caracteres")
    String coffee_type,

    @JsonProperty("processing_method")
    @NotBlank(message = "El método de procesamiento es obligatorio")
    @Size(max = 50, message = "El método de procesamiento no puede superar 50 caracteres")
    String processing_method,

    @JsonProperty("altitude")
    @NotNull(message = "La altitud es obligatoria")
    @Min(value = 0, message = "La altitud debe ser al menos 0 msnm")
    @Max(value = 2500, message = "La altitud no puede superar 2500 msnm")
    Integer altitude,

    @JsonProperty("weight")
    @NotNull(message = "El peso es obligatorio")
    @DecimalMin(value = "1.0", message = "El peso mínimo es 1 kg")
    @DecimalMax(value = "70.0", message = "El peso máximo es 70 kg")
    @Digits(integer = 2, fraction = 2, message = "El peso admite máximo 2 decimales")
    Double weight,

    @JsonProperty("origin")
    @NotBlank(message = "El origen es obligatorio")
    @Size(max = 100, message = "El origen no puede superar 100 caracteres")
    String origin,

    @JsonProperty("status")
    @NotBlank(message = "El estado es obligatorio")
    @Size(max = 20, message = "El estado no puede superar 20 caracteres")
    String status,

    @JsonProperty("certifications")
    List<String> certifications
) {}
