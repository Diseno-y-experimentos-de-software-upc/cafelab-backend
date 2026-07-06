package com.cafemetrix.cafelab.production.interfaces.rest.transform;

import com.cafemetrix.cafelab.production.domain.model.commands.CreateCoffeeLotVersionCommand;
import com.cafemetrix.cafelab.production.interfaces.rest.resources.UpdateCoffeeLotResource;

public class CreateCoffeeLotVersionCommandFromResourceAssembler {
    public static CreateCoffeeLotVersionCommand toCommandFromResource(Long coffeeLotId, UpdateCoffeeLotResource resource) {
        return new CreateCoffeeLotVersionCommand(
            coffeeLotId,
            resource.lot_name(),
            resource.coffee_type(),
            resource.processing_method(),
            resource.altitude(),
            resource.origin(),
            resource.status(),
            resource.certifications()
        );
    }
}
