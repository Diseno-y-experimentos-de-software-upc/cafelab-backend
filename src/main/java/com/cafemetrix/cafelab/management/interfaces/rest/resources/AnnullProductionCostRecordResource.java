package com.cafemetrix.cafelab.management.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Body de la petición de anulación. {@code reason} corresponde al motivo elegido en el
 * selector del frontend o, cuando se elige «otro», al texto libre limitado a 25 caracteres.
 */
public record AnnullProductionCostRecordResource(
        @NotBlank(message = "El motivo es obligatorio")
                @Size(max = 25, message = "El motivo no puede superar 25 caracteres")
                String reason) {}
