package com.cafemetrix.cafelab.defects.domain.model.commands;

public record UpdateDefectCommand(
        Long defectId,
        Long userId,
        String coffeeDisplayName,
        String coffeeRegion,
        String coffeeVariety,
        Double coffeeTotalWeight,
        String name,
        String defectType,
        Double defectWeight,
        Double percentage,
        String probableCause,
        String suggestedSolution) {}
