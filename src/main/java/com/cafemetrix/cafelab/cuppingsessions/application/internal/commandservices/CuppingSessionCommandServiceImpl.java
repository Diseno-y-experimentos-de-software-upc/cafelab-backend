package com.cafemetrix.cafelab.cuppingsessions.application.internal.commandservices;

import com.cafemetrix.cafelab.cuppingsessions.domain.model.aggregates.CuppingSession;
import com.cafemetrix.cafelab.cuppingsessions.domain.model.commands.CreateCuppingSessionCommand;
import com.cafemetrix.cafelab.cuppingsessions.domain.model.commands.UpdateCuppingSessionCommand;
import com.cafemetrix.cafelab.cuppingsessions.domain.services.CuppingSessionCommandService;
import com.cafemetrix.cafelab.cuppingsessions.infrastructure.persistence.jpa.repositories.CuppingSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
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
        assertSessionDateForCreate(command.sessionDate());
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

    /**
     * En el alta, la fecha de sesión no puede ser anterior al día calendario actual (zona por defecto de la JVM).
     * La fecha no se modifica en actualizaciones ({@link CuppingSession#applyUpdate}).
     */
    private static void assertSessionDateForCreate(LocalDate sessionDate) {
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        if (sessionDate.isBefore(today)) {
            throw new IllegalArgumentException(
                    "La fecha de la sesión no puede ser anterior al día actual. Elija hoy o una fecha futura.");
        }
    }

    @Override
    @Transactional
    public boolean delete(Long sessionId, Long userId) {
        return repository
                .findByIdAndUserId(sessionId, userId)
                .map(
                        entity -> {
                            entity.softDelete();
                            repository.save(entity);
                            return true;
                        })
                .orElse(false);
    }
}
