package com.cafemetrix.cafelab.management.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * POST sin userId en cuerpo (lo fija el JWT). Descuenta {@code quantityUsed} del peso del lote.
 */
public record CreateInventoryEntryResource(
        @JsonProperty("coffeeLotId")
        @NotNull(message = "Debe seleccionar un lote")
        @Positive(message = "Lote inválido")
        Long coffeeLotId,

        @NotNull(message = "La cantidad consumida es obligatoria")
        @Positive(message = "La cantidad debe ser mayor que cero")
        Double quantityUsed,

        @NotNull(message = "La fecha es obligatoria")
        LocalDateTime dateUsed,

        @NotBlank(message = "El producto final es obligatorio")
        @Size(max = 100, message = "El producto final no puede superar 100 caracteres")
        String finalProduct,

        @Size(max = 50, message = "El motivo de consumo no puede superar 50 caracteres")
        String consumptionReason,

        @Size(max = 1000, message = "Las notas de uso no pueden superar 1000 caracteres")
        String usageNotes) {}
