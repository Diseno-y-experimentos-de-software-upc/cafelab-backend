package com.cafemetrix.cafelab.production.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonProperty;

/** JSON usa {@code lot} (mismo nombre que en create/update) para el id del lote. */
public record RoastProfileResource(
    Long id,
    Long userId,
    String name,
    String type,
    Integer duration,
    Double tempStart,
    Double tempEnd,
    @JsonProperty("lot")
    Long coffeeLotId,
    Boolean isFavorite
) {}
