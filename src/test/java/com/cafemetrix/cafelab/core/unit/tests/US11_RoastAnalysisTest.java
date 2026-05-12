package com.cafemetrix.cafelab.core.unit.tests;

import com.cafemetrix.cafelab.production.application.internal.queryservices.RoastProfileQueryServiceImpl;
import com.cafemetrix.cafelab.production.domain.model.aggregates.RoastProfile;
import com.cafemetrix.cafelab.production.domain.model.commands.CreateRoastProfileCommand;
import com.cafemetrix.cafelab.production.domain.model.queries.GetRoastProfilesByUserIdQuery;
import com.cafemetrix.cafelab.production.infrastructure.persistence.jpa.repositories.RoastProfileRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("US11 - Análisis de Tuestes")
class US11_RoastAnalysisTest {

    @Test
    @DisplayName("recupera perfiles por usuario para comparar sesiones de tueste")
    void queriesRoastProfilesByUserForComparison() {
        var repository = mock(RoastProfileRepository.class);
        var service = new RoastProfileQueryServiceImpl(repository);
        
        // Fix: Usar RoastType válido ("Ligero", "Medio")
        var light = new RoastProfile(new CreateRoastProfileCommand(
                7L, "Claro", "Ligero", 10, 150.0, 205.0, 44L, false));
        var medium = new RoastProfile(new CreateRoastProfileCommand(
                7L, "Medio", "Medio", 13, 155.0, 215.0, 44L, true));
        
        when(repository.findByUserId(7L)).thenReturn(List.of(light, medium));

        var result = service.handle(new GetRoastProfilesByUserIdQuery(7L));

        assertThat(result).extracting(RoastProfile::getName).containsExactly("Claro", "Medio");
        assertThat(result).extracting(RoastProfile::getDuration).containsExactly(10, 13);
        verify(repository).findByUserId(7L);
    }
}
