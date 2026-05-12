package com.cafemetrix.cafelab.management.interfaces.acl;

import com.cafemetrix.cafelab.management.domain.model.aggregates.InventoryEntry;
import com.cafemetrix.cafelab.management.domain.model.aggregates.ProductionCostRecord;
import com.cafemetrix.cafelab.management.domain.model.commands.CreateInventoryEntryCommand;
import com.cafemetrix.cafelab.management.domain.model.commands.CreateProductionCostRecordCommand;
import com.cafemetrix.cafelab.management.domain.model.commands.UpdateProductionCostRecordCommand;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Anti-corruption facade for the management bounded context (inventory, production cost records, stock
 * coordination).
 * Implementations live in {@code application.acl}; controllers depend on this interface only.
 */
public interface ManagementContextFacade {

    Long createInventoryEntry(CreateInventoryEntryCommand command);

    Long updateInventoryEntry(
            Long ownerUserId,
            Long inventoryEntryId,
            Long coffeeLotId,
            Double quantityUsed,
            LocalDateTime dateUsed,
            String finalProduct);

    boolean deleteInventoryEntry(Long ownerUserId, Long inventoryEntryId);

    List<InventoryEntry> getAllInventoryEntries();

    List<InventoryEntry> getInventoryEntriesByUserId(Long userId);

    List<InventoryEntry> getInventoryEntriesByCoffeeLotId(Long coffeeLotId);

    Optional<InventoryEntry> getInventoryEntryById(Long inventoryEntryId);

    Long createProductionCostRecord(CreateProductionCostRecordCommand command);

    Long updateProductionCostRecord(Long ownerUserId, UpdateProductionCostRecordCommand command);

    /**
     * Marca el registro como {@code anulado} con el motivo provisto. No borra la fila para que la
     * información quede disponible en auditorías.
     *
     * @return id del registro afectado, o {@code 0L} si no existe / no pertenece al usuario.
     */
    Long annullProductionCostRecord(Long ownerUserId, Long id, String reason);

    boolean deleteProductionCostRecord(Long ownerUserId, Long id);

    List<ProductionCostRecord> getProductionCostRecordsByUserId(Long userId);

    Optional<ProductionCostRecord> getProductionCostRecordById(Long id);

    Optional<ProductionCostRecord> getProductionCostRecordByIdAndUserId(Long id, Long userId);
}
