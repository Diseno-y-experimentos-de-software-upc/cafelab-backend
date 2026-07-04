package com.cafemetrix.cafelab.cuppingsessions.domain.model.commands;

import java.time.LocalDate;

public record CreateCuppingSessionCommand(
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
