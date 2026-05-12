package com.cafemetrix.cafelab.management.application.internal.commandservices;

import com.cafemetrix.cafelab.management.domain.model.aggregates.ProductionCostRecord;
import com.cafemetrix.cafelab.management.domain.model.commands.AnnullProductionCostRecordCommand;
import com.cafemetrix.cafelab.management.domain.model.commands.CreateProductionCostRecordCommand;
import com.cafemetrix.cafelab.management.domain.model.commands.DeleteProductionCostRecordCommand;
import com.cafemetrix.cafelab.management.domain.model.commands.UpdateProductionCostRecordCommand;
import com.cafemetrix.cafelab.management.domain.services.ProductionCostRecordCommandService;
import com.cafemetrix.cafelab.management.infrastructure.persistence.jpa.repositories.ProductionCostRecordRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductionCostRecordCommandServiceImpl implements ProductionCostRecordCommandService {
    private final ProductionCostRecordRepository productionCostRecordRepository;

    public ProductionCostRecordCommandServiceImpl(ProductionCostRecordRepository productionCostRecordRepository) {
        this.productionCostRecordRepository = productionCostRecordRepository;
    }

    @Override
    public Optional<ProductionCostRecord> handle(CreateProductionCostRecordCommand command) {
        var entity = new ProductionCostRecord(command);
        return Optional.of(productionCostRecordRepository.save(entity));
    }

    @Override
    public Optional<ProductionCostRecord> handle(UpdateProductionCostRecordCommand command) {
        return productionCostRecordRepository
                .findById(command.id())
                .map(existing -> productionCostRecordRepository.save(existing.update(command)));
    }

    @Override
    public Optional<ProductionCostRecord> handle(AnnullProductionCostRecordCommand command) {
        return productionCostRecordRepository
                .findById(command.id())
                .map(existing -> productionCostRecordRepository.save(existing.annull(command.reason())));
    }

    @Override
    public boolean handle(DeleteProductionCostRecordCommand command) {
        return productionCostRecordRepository
                .findById(command.id())
                .map(
                        e -> {
                            productionCostRecordRepository.deleteById(command.id());
                            return true;
                        })
                .orElse(false);
    }
}
