package com.cafemetrix.cafelab.defects.interfaces.rest.transform;

import com.cafemetrix.cafelab.defects.domain.model.commands.CreateDefectCommand;
import com.cafemetrix.cafelab.defects.interfaces.rest.resources.CreateDefectResource;

public class CreateDefectCommandFromResourceAssembler {

    public static CreateDefectCommand toCommandFromResource(Long userId, CreateDefectResource resource) {
        return new CreateDefectCommand(
                userId,
                resource.coffeeDisplayName().trim(),
                blankToNull(resource.coffeeRegion()),
                resource.coffeeVariety().trim(),
                resource.coffeeTotalWeight(),
                resource.name(),
                resource.defectType(),
                resource.defectWeight(),
                resource.percentage(),
                resource.probableCause(),
                resource.suggestedSolution()
        );
    }

    private static String blankToNull(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        return s.trim();
    }
}
