package com.cafemetrix.cafelab.cuppingsessions.interfaces.rest.transform;

import com.cafemetrix.cafelab.cuppingsessions.domain.model.commands.UpdateCuppingSessionCommand;
import com.cafemetrix.cafelab.cuppingsessions.interfaces.rest.resources.UpdateCuppingSessionResource;

public class UpdateCuppingSessionCommandFromResourceAssembler {

    public static UpdateCuppingSessionCommand toCommand(
            Long sessionId, Long userId, UpdateCuppingSessionResource r) {
        return new UpdateCuppingSessionCommand(
                sessionId,
                userId,
                r.name(),
                r.origin(),
                r.variety(),
                r.processing(),
                r.sessionDate(),
                r.favorite(),
                r.resultsJson(),
                r.roastStyleNotes(),
                r.coffeeLotId(),
                r.roastProfileId());
    }
}
