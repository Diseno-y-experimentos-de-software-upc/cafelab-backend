package com.cafemetrix.cafelab.cuppingsessions.infrastructure.persistence.jpa.repositories;

import com.cafemetrix.cafelab.cuppingsessions.domain.model.aggregates.CuppingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CuppingSessionRepository extends JpaRepository<CuppingSession, Long> {

    List<CuppingSession> findByUserIdOrderBySessionDateDescCreatedAtDesc(Long userId);

    List<CuppingSession> findByUserIdAndCoffeeLotIdOrderBySessionDateDescCreatedAtDesc(Long userId, Long coffeeLotId);

    Optional<CuppingSession> findByIdAndUserId(Long id, Long userId);
}
