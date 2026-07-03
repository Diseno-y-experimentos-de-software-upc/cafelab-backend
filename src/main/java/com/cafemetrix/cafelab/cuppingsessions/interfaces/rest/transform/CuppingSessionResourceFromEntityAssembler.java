package com.cafemetrix.cafelab.cuppingsessions.interfaces.rest.transform;

import com.cafemetrix.cafelab.cuppingsessions.domain.model.aggregates.CuppingSession;
import com.cafemetrix.cafelab.cuppingsessions.interfaces.rest.resources.CuppingSessionResource;

public class CuppingSessionResourceFromEntityAssembler {

    public static CuppingSessionResource toResource(CuppingSession e) {
        return new CuppingSessionResource(
                e.getId(),
                e.getUserId(),
                e.getName(),
                e.getOrigin(),
                e.getVariety(),
                e.getProcessing(),
                e.getSessionDate(),
                e.isFavorite(),
                e.getResultsJson(),
                e.getRoastStyleNotes(),
                e.getCoffeeLotId(),
                e.getRoastProfileId());
    }
}
