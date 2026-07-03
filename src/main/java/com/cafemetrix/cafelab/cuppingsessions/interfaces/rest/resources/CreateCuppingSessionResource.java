package com.cafemetrix.cafelab.cuppingsessions.interfaces.rest.resources;

import java.time.LocalDate;

public record CreateCuppingSessionResource(
        String name,
        String origin,
        String variety,
        String processing,
        LocalDate sessionDate,
        Boolean favorite,
        String resultsJson,
        String roastStyleNotes,
        Long coffeeLotId,
        Long roastProfileId
) {
    public CreateCuppingSessionResource {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        if (origin == null || origin.isBlank()) {
            throw new IllegalArgumentException("origin is required");
        }
        if (variety == null || variety.isBlank()) {
            throw new IllegalArgumentException("variety is required");
        }
        if (processing == null || processing.isBlank()) {
            throw new IllegalArgumentException("processing is required");
        }
        if (sessionDate == null) {
            throw new IllegalArgumentException("sessionDate is required");
        }
    }

    public boolean favoriteOrDefault() {
        return Boolean.TRUE.equals(favorite);
    }
}
