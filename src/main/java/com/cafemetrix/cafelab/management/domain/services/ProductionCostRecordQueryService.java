package com.cafemetrix.cafelab.management.domain.services;

import com.cafemetrix.cafelab.management.domain.model.aggregates.ProductionCostRecord;

import java.util.List;
import java.util.Optional;

public interface ProductionCostRecordQueryService {
    List<ProductionCostRecord> getProductionCostRecordsByUserId(Long userId);

    Optional<ProductionCostRecord> getProductionCostRecordById(Long id);

    Optional<ProductionCostRecord> getProductionCostRecordByIdAndUserId(Long id, Long userId);
}
