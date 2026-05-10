package com.cafemetrix.cafelab.management.application.internal.queryservices;

import com.cafemetrix.cafelab.management.domain.model.aggregates.ProductionCostRecord;
import com.cafemetrix.cafelab.management.domain.services.ProductionCostRecordQueryService;
import com.cafemetrix.cafelab.management.infrastructure.persistence.jpa.repositories.ProductionCostRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductionCostRecordQueryServiceImpl implements ProductionCostRecordQueryService {
    private final ProductionCostRecordRepository productionCostRecordRepository;

    public ProductionCostRecordQueryServiceImpl(ProductionCostRecordRepository productionCostRecordRepository) {
        this.productionCostRecordRepository = productionCostRecordRepository;
    }

    @Override
    public List<ProductionCostRecord> getProductionCostRecordsByUserId(Long userId) {
        return productionCostRecordRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public Optional<ProductionCostRecord> getProductionCostRecordById(Long id) {
        return productionCostRecordRepository.findById(id);
    }

    @Override
    public Optional<ProductionCostRecord> getProductionCostRecordByIdAndUserId(Long id, Long userId) {
        return productionCostRecordRepository.findByIdAndUserId(id, userId);
    }
}
