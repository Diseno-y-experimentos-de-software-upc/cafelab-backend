package com.cafemetrix.cafelab.management.application.internal.commandservices;

import com.cafemetrix.cafelab.management.domain.model.aggregates.InventoryEntry;
import com.cafemetrix.cafelab.management.domain.model.commands.CreateInventoryEntryCommand;
import com.cafemetrix.cafelab.management.domain.model.commands.DeleteInventoryEntryCommand;
import com.cafemetrix.cafelab.management.domain.model.commands.UpdateInventoryEntryCommand;
import com.cafemetrix.cafelab.management.domain.services.InventoryEntryCommandService;
import com.cafemetrix.cafelab.management.infrastructure.persistence.jpa.repositories.InventoryEntryRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InventoryEntryCommandServiceImpl implements InventoryEntryCommandService {
    private final InventoryEntryRepository inventoryEntryRepository;

    public InventoryEntryCommandServiceImpl(InventoryEntryRepository inventoryEntryRepository) {
        this.inventoryEntryRepository = inventoryEntryRepository;
    }

    @Override
    public Optional<InventoryEntry> handle(CreateInventoryEntryCommand command) {
        var inventoryEntry = new InventoryEntry(command);
        var savedInventoryEntry = inventoryEntryRepository.save(inventoryEntry);
        return Optional.of(savedInventoryEntry);
    }

    @Override
    public Optional<InventoryEntry> handle(UpdateInventoryEntryCommand command) {
        var inventoryEntry = inventoryEntryRepository.findById(command.inventoryEntryId());
        if (inventoryEntry.isPresent()) {
            var updatedInventoryEntry = inventoryEntry.get().update(command);
            var savedInventoryEntry = inventoryEntryRepository.save(updatedInventoryEntry);
            return Optional.of(savedInventoryEntry);
        }
        return Optional.empty();
    }

    @Override
    public boolean handle(DeleteInventoryEntryCommand command) {
        var inventoryEntry = inventoryEntryRepository.findById(command.inventoryEntryId());
        if (inventoryEntry.isPresent()) {
            inventoryEntry.get().softDelete();
            inventoryEntryRepository.save(inventoryEntry.get());
            return true;
        }
        return false;
    }
}
