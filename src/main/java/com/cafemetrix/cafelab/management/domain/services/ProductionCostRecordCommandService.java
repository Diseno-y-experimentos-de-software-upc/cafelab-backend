package com.cafemetrix.cafelab.management.domain.services;

import com.cafemetrix.cafelab.management.domain.model.aggregates.ProductionCostRecord;
import com.cafemetrix.cafelab.management.domain.model.commands.AnnullProductionCostRecordCommand;
import com.cafemetrix.cafelab.management.domain.model.commands.CreateProductionCostRecordCommand;
import com.cafemetrix.cafelab.management.domain.model.commands.DeleteProductionCostRecordCommand;
import com.cafemetrix.cafelab.management.domain.model.commands.UpdateProductionCostRecordCommand;

import java.util.Optional;

public interface ProductionCostRecordCommandService {
    Optional<ProductionCostRecord> handle(CreateProductionCostRecordCommand command);

    Optional<ProductionCostRecord> handle(UpdateProductionCostRecordCommand command);

    Optional<ProductionCostRecord> handle(AnnullProductionCostRecordCommand command);

    boolean handle(DeleteProductionCostRecordCommand command);
}
