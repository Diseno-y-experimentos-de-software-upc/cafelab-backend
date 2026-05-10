package com.cafemetrix.cafelab.management.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateProductionCostRecordResource(
        @NotNull(message = "Debe seleccionar un lote") @Positive(message = "Lote inválido") Long coffeeLotId,
        @NotBlank(message = "La moneda es obligatoria")
                @Pattern(regexp = "PEN|USD", message = "Moneda debe ser PEN o USD")
                @Size(max = 3)
                String currency,
        @NotNull(message = "La cantidad (kg) es obligatoria") @Positive(message = "La cantidad debe ser mayor que cero")
                Double totalKg,
        Double marginPercent,
        @NotNull(message = "Costo materia prima obligatorio") Double rawMaterialsCost,
        @NotNull(message = "Costo mano de obra obligatorio") Double laborCost,
        @NotNull(message = "Costo transporte obligatorio") Double transportCost,
        @NotNull(message = "Costo almacenamiento obligatorio") Double storageCost,
        @NotNull(message = "Costo procesamiento obligatorio") Double processingCost,
        @NotNull(message = "Otros costos indirectos obligatorios") Double otherIndirectCosts) {}
