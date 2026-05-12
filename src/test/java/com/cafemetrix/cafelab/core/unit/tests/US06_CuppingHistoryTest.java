package com.cafemetrix.cafelab.core.unit.tests;

import com.cafemetrix.cafelab.cuppingsessions.application.internal.queryservices.CuppingSessionQueryServiceImpl;
import com.cafemetrix.cafelab.cuppingsessions.domain.model.aggregates.CuppingSession;
import com.cafemetrix.cafelab.cuppingsessions.domain.model.commands.CreateCuppingSessionCommand;
import com.cafemetrix.cafelab.cuppingsessions.domain.model.queries.GetCuppingSessionsByUserIdQuery;
import com.cafemetrix.cafelab.cuppingsessions.infrastructure.persistence.jpa.repositories.CuppingSessionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("US06 - Historial de Catas")
class US06_CuppingHistoryTest {

    @Test
    @DisplayName("consulta el historial de cuppings por usuario")
    void queriesCuppingHistoryByUser() {
        var repository = mock(CuppingSessionRepository.class);
        var service = new CuppingSessionQueryServiceImpl(repository);
        var session = new CuppingSession(new CreateCuppingSessionCommand(
                7L, "Cupping 1", "Jaen", "Caturra", "washed",
                LocalDate.of(2026, 5, 12), false, "{}", null));
        when(repository.findByUserIdOrderBySessionDateDescCreatedAtDesc(7L))
                .thenReturn(List.of(session));

        var result = service.handle(new GetCuppingSessionsByUserIdQuery(7L));

        assertThat(result).containsExactly(session);
        verify(repository).findByUserIdOrderBySessionDateDescCreatedAtDesc(7L);
    }
}
