package com.cafemetrix.cafelab.cuppingsessions.interfaces.rest.transform;

import com.cafemetrix.cafelab.cuppingsessions.domain.model.commands.CreateCuppingSessionCommand;
import com.cafemetrix.cafelab.cuppingsessions.interfaces.rest.resources.CreateCuppingSessionResource;

public class CreateCuppingSessionCommandFromResourceAssembler {

    public static CreateCuppingSessionCommand toCommand(Long userId, CreateCuppingSessionResource r) {
        return new CreateCuppingSessionCommand(
                userId,
                r.name(),
                r.origin(),
                r.variety(),
                r.processing(),
                r.sessionDate(),
                r.favoriteOrDefault(),
                r.resultsJson(),
                r.roastStyleNotes(),
                r.coffeeLotId(),
                r.roastProfileId());
    }
}
