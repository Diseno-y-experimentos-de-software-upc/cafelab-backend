package com.cafemetrix.cafelab.management.interfaces.rest.transform;

import com.cafemetrix.cafelab.management.domain.model.commands.CreateInventoryEntryCommand;
import com.cafemetrix.cafelab.management.interfaces.rest.resources.CreateInventoryEntryResource;

public final class CreateInventoryEntryCommandFromResourceAssembler {
    private CreateInventoryEntryCommandFromResourceAssembler() {}

    public static CreateInventoryEntryCommand toCommandFromResource(
            Long ownerUserId, CreateInventoryEntryResource resource) {
        return new CreateInventoryEntryCommand(
                ownerUserId,
                resource.coffeeLotId(),
                resource.quantityUsed(),
                resource.dateUsed(),
                resource.finalProduct(),
                resource.motivoDeConsumo(),
                resource.notasDeUso());
    }
}
