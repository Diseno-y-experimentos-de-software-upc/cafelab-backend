package com.cafemetrix.cafelab.cuppingsessions.application.internal.commandservices;

import com.cafemetrix.cafelab.cuppingsessions.domain.model.aggregates.CuppingSession;
import com.cafemetrix.cafelab.cuppingsessions.domain.model.commands.CreateCuppingSessionCommand;
import com.cafemetrix.cafelab.cuppingsessions.domain.model.commands.UpdateCuppingSessionCommand;
import com.cafemetrix.cafelab.cuppingsessions.domain.services.CuppingSessionCommandService;
import com.cafemetrix.cafelab.cuppingsessions.infrastructure.persistence.jpa.repositories.CuppingSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CuppingSessionCommandServiceImpl implements CuppingSessionCommandService {
    private final CuppingSessionRepository repository;

    public CuppingSessionCommandServiceImpl(CuppingSessionRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Optional<CuppingSession> handle(CreateCuppingSessionCommand command) {
        return Optional.of(repository.save(new CuppingSession(command)));
    }

    @Override
    @Transactional
    public Optional<CuppingSession> handle(UpdateCuppingSessionCommand command) {
        return repository
                .findByIdAndUserId(command.sessionId(), command.userId())
                .map(
                        entity -> {
                            entity.applyUpdate(command);
                            return repository.save(entity);
                        });
    }

    @Override
    @Transactional
    public boolean delete(Long sessionId, Long userId) {
        return repository
                .findByIdAndUserId(sessionId, userId)
                .map(
                        entity -> {
                            repository.delete(entity);
                            return true;
                        })
                .orElse(false);
    }
}
