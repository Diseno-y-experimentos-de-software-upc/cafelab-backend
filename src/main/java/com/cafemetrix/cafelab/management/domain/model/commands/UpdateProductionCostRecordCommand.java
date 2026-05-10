package com.cafemetrix.cafelab.management.domain.model.commands;

public record UpdateProductionCostRecordCommand(
        Long id,
        Long coffeeLotId,
        String lotName,
        String coffeeType,
        String currency,
        Double totalKg,
        Double marginPercent,
        Double rawMaterialsCost,
        Double laborCost,
        Double transportCost,
        Double storageCost,
        Double processingCost,
        Double otherIndirectCosts,
        Double totalDirectCost,
        Double totalIndirectCost,
        Double totalCost,
        Double costPerKg,
        Double suggestedPrice,
        Double potentialMargin) {}
