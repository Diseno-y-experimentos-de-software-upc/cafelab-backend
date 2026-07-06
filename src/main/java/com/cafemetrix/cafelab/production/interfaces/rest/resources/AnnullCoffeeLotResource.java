package com.cafemetrix.cafelab.production.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AnnullCoffeeLotResource(
        @NotBlank(message = "El motivo es obligatorio")
                @Size(max = 25, message = "El motivo no puede superar 25 caracteres")
                String reason) {}
