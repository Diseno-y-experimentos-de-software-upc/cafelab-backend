package com.cafemetrix.cafelab.management.infrastructure.persistence.jpa.repositories;

import com.cafemetrix.cafelab.management.domain.model.aggregates.ProductionCostRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductionCostRecordRepository extends JpaRepository<ProductionCostRecord, Long> {
    List<ProductionCostRecord> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<ProductionCostRecord> findByIdAndUserId(Long id, Long userId);
}
