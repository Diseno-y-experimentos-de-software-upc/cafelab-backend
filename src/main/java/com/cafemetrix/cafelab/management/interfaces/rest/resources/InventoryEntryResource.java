package com.cafemetrix.cafelab.management.interfaces.rest.resources;

import java.time.LocalDateTime;

/** DTO; {@code userId} corresponde a columna {@code user_id} en BD. */
public record InventoryEntryResource(
    Long id,
    Long userId,
    Long coffeeLotId,
    Double quantityUsed,
    LocalDateTime dateUsed,
    String finalProduct,
    String motivoDeConsumo,
    String notasDeUso
) {}
