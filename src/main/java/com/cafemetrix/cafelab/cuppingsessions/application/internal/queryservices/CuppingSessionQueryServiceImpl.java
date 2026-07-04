package com.cafemetrix.cafelab.cuppingsessions.application.internal.queryservices;

import com.cafemetrix.cafelab.cuppingsessions.domain.model.aggregates.CuppingSession;
import com.cafemetrix.cafelab.cuppingsessions.domain.model.queries.GetCuppingSessionByIdForUserQuery;
import com.cafemetrix.cafelab.cuppingsessions.domain.model.queries.GetCuppingSessionsByCoffeeLotIdQuery;
import com.cafemetrix.cafelab.cuppingsessions.domain.model.queries.GetCuppingSessionsByUserIdQuery;
import com.cafemetrix.cafelab.cuppingsessions.domain.services.CuppingSessionQueryService;
import com.cafemetrix.cafelab.cuppingsessions.infrastructure.persistence.jpa.repositories.CuppingSessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CuppingSessionQueryServiceImpl implements CuppingSessionQueryService {
    private final CuppingSessionRepository repository;

    public CuppingSessionQueryServiceImpl(CuppingSessionRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<CuppingSession> handle(GetCuppingSessionsByUserIdQuery query) {
        return repository.findByUserIdOrderBySessionDateDescCreatedAtDesc(query.userId());
    }

    @Override
    public List<CuppingSession> handle(GetCuppingSessionsByCoffeeLotIdQuery query) {
        return repository.findByUserIdAndCoffeeLotIdOrderBySessionDateDescCreatedAtDesc(
                query.userId(), query.coffeeLotId());
    }

    @Override
    public Optional<CuppingSession> handle(GetCuppingSessionByIdForUserQuery query) {
        return repository.findByIdAndUserId(query.sessionId(), query.userId());
    }
}
