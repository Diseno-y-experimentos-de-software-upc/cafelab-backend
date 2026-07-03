package com.cafemetrix.cafelab.cuppingsessions.domain.services;

import com.cafemetrix.cafelab.cuppingsessions.domain.model.aggregates.CuppingSession;
import com.cafemetrix.cafelab.cuppingsessions.domain.model.queries.GetCuppingSessionByIdForUserQuery;
import com.cafemetrix.cafelab.cuppingsessions.domain.model.queries.GetCuppingSessionsByCoffeeLotIdQuery;
import com.cafemetrix.cafelab.cuppingsessions.domain.model.queries.GetCuppingSessionsByUserIdQuery;

import java.util.List;
import java.util.Optional;

public interface CuppingSessionQueryService {

    List<CuppingSession> handle(GetCuppingSessionsByUserIdQuery query);

    List<CuppingSession> handle(GetCuppingSessionsByCoffeeLotIdQuery query);

    Optional<CuppingSession> handle(GetCuppingSessionByIdForUserQuery query);
}
