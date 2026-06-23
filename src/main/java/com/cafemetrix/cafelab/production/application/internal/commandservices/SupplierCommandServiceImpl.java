package com.cafemetrix.cafelab.production.application.internal.commandservices;

import com.cafemetrix.cafelab.production.domain.model.aggregates.Supplier;
import com.cafemetrix.cafelab.production.domain.model.commands.CreateSupplierCommand;
import com.cafemetrix.cafelab.production.domain.model.commands.DeleteSupplierCommand;
import com.cafemetrix.cafelab.production.domain.model.commands.UpdateSupplierCommand;
import com.cafemetrix.cafelab.production.domain.services.SupplierCommandService;
import com.cafemetrix.cafelab.production.infrastructure.persistence.jpa.repositories.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SupplierCommandServiceImpl implements SupplierCommandService {
    private final SupplierRepository supplierRepository;

    public SupplierCommandServiceImpl(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Override
    public Optional<Supplier> handle(CreateSupplierCommand command) {
        if (supplierRepository.existsByNameValueAndUserId(command.name(), command.userId())) {
            throw new IllegalArgumentException(
                    "Ya existe un proveedor con el nombre \"" + command.name() + "\". Use un nombre diferente.");
        }
        try {
            var supplier = new Supplier(command);
            return Optional.of(supplierRepository.save(supplier));
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Supplier> handle(UpdateSupplierCommand command) {
        var existingSupplier = supplierRepository.findById(command.supplierId());
        if (existingSupplier.isEmpty()) {
            return Optional.empty();
        }
        Long userId = existingSupplier.get().getUserId();
        if (supplierRepository.existsByNameAndUserIdExcluding(command.name(), userId, command.supplierId())) {
            throw new IllegalArgumentException(
                    "Ya existe un proveedor con el nombre \"" + command.name() + "\". Use un nombre diferente.");
        }
        try {
            var supplier = existingSupplier.get();
            supplier.update(command);
            return Optional.of(supplierRepository.save(supplier));
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean handle(DeleteSupplierCommand command) {
        var supplier = supplierRepository.findById(command.supplierId());
        if (supplier.isEmpty()) {
            return false;
        }
        supplier.get().softDelete();
        supplierRepository.save(supplier.get());
        return true;
    }
}
