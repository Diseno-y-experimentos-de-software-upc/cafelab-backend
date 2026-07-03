package com.cafemetrix.cafelab.production.interfaces.rest.transform;

import com.cafemetrix.cafelab.production.domain.model.commands.UpdateSupplierCommand;
import com.cafemetrix.cafelab.production.interfaces.rest.resources.UpdateSupplierResource;

public class UpdateSupplierCommandFromResourceAssembler {
    public static UpdateSupplierCommand toCommandFromResource(Long supplierId, UpdateSupplierResource resource) {
        return new UpdateSupplierCommand(
            supplierId,
            resource.name(),
            resource.email(),
            resource.phone(),
            resource.location(),
            resource.specialties(),
            resource.contactPerson(),
            resource.webLink()
        );
    }
}
