package com.cafemetrix.cafelab.cuppingsessions.interfaces.rest.resources;

import java.time.LocalDate;

public record CuppingSessionResource(
        Long id,
        Long userId,
        String name,
        String origin,
        String variety,
        String processing,
        LocalDate sessionDate,
        boolean favorite,
        String resultsJson,
        String roastStyleNotes,
        Long coffeeLotId,
        Long roastProfileId
) {
}
