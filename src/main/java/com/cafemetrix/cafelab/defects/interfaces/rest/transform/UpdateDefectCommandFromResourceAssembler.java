package com.cafemetrix.cafelab.defects.interfaces.rest.transform;

import com.cafemetrix.cafelab.defects.domain.model.commands.UpdateDefectCommand;
import com.cafemetrix.cafelab.defects.interfaces.rest.resources.CreateDefectResource;

public final class UpdateDefectCommandFromResourceAssembler {

    private UpdateDefectCommandFromResourceAssembler() {}

    public static UpdateDefectCommand toCommand(Long defectId, Long userId, CreateDefectResource resource) {
        return new UpdateDefectCommand(
                defectId,
                userId,
                resource.coffeeDisplayName().trim(),
                blankToNull(resource.coffeeRegion()),
                resource.coffeeVariety().trim(),
                resource.coffeeTotalWeight(),
                resource.name().trim(),
                resource.defectType().trim(),
                resource.defectWeight(),
                resource.percentage(),
                resource.probableCause().trim(),
                resource.suggestedSolution().trim());
    }

    private static String blankToNull(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        return s.trim();
    }
}
