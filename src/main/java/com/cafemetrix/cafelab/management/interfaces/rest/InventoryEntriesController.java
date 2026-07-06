package com.cafemetrix.cafelab.management.interfaces.rest;

import com.cafemetrix.cafelab.management.domain.exceptions.InventoryEntryNotFoundException;
import com.cafemetrix.cafelab.management.domain.model.aggregates.InventoryEntry;
import com.cafemetrix.cafelab.management.interfaces.acl.ManagementContextFacade;
import com.cafemetrix.cafelab.management.interfaces.rest.resources.CreateInventoryEntryResource;
import com.cafemetrix.cafelab.management.interfaces.rest.resources.InventoryEntryResource;
import com.cafemetrix.cafelab.management.interfaces.rest.resources.UpdateInventoryEntryResource;
import com.cafemetrix.cafelab.management.interfaces.rest.transform.CreateInventoryEntryCommandFromResourceAssembler;
import com.cafemetrix.cafelab.management.interfaces.rest.transform.UpdateInventoryEntryCommandFromResourceAssembler;
import com.cafemetrix.cafelab.production.interfaces.acl.CoffeeproductionContextFacade;
import com.cafemetrix.cafelab.iam.infrastructure.authorization.sfs.support.CurrentProfileIdResolver;
import com.cafemetrix.cafelab.shared.interfaces.rest.resources.MessageResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/inventory-entries", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Inventory Entries", description = "Inventory Entry Management Endpoints")
public class InventoryEntriesController {
    private final ManagementContextFacade managementContextFacade;
    private final CoffeeproductionContextFacade coffeeproductionContextFacade;
    private final CurrentProfileIdResolver currentProfileIdResolver;

    public InventoryEntriesController(
            ManagementContextFacade managementContextFacade,
            CoffeeproductionContextFacade coffeeproductionContextFacade,
            CurrentProfileIdResolver currentProfileIdResolver) {
        this.managementContextFacade = managementContextFacade;
        this.coffeeproductionContextFacade = coffeeproductionContextFacade;
        this.currentProfileIdResolver = currentProfileIdResolver;
    }

    private Optional<Long> resolveCurrentUserId() {
        return currentProfileIdResolver.resolveProfileId();
    }

    private ResponseEntity<?> forbidden(String message) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResource(message));
    }

    private ResponseEntity<?> unauthorized(String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResource(message));
    }

    private boolean ownsCoffeeLot(Long coffeeLotId, Long currentUserId) {
        return coffeeproductionContextFacade
                .getCoffeeLotById(coffeeLotId)
                .map(l -> l.getUserId().equals(currentUserId))
                .orElse(false);
    }

    private boolean ownsInventoryEntry(Long inventoryEntryId, Long currentUserId) {
        return managementContextFacade
                .getInventoryEntryById(inventoryEntryId)
                .map(e -> e.getUserId().equals(currentUserId))
                .orElse(false);
    }

    private InventoryEntryResource toResource(InventoryEntry entry) {
        return new InventoryEntryResource(
                entry.getId(),
                entry.getUserId(),
                entry.getCoffeeLotId(),
                entry.getQuantityUsed(),
                entry.getDateUsed(),
                entry.getFinalProduct(),
                entry.getConsumptionReason(),
                entry.getUsageNotes());
    }

    @Operation(summary = "Registrar consumo (perfil desde JWT; descuenta stock del lote)")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createInventoryEntry(@Valid @RequestBody CreateInventoryEntryResource resource) {
        Optional<Long> ownerIdOpt = resolveCurrentUserId();
        if (ownerIdOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }
        Long ownerId = ownerIdOpt.get();
        if (!ownsCoffeeLot(resource.coffeeLotId(), ownerId)) {
            return forbidden("Debe consumir de un lote de su cuenta");
        }
        var command =
                CreateInventoryEntryCommandFromResourceAssembler.toCommandFromResource(ownerId, resource);
        var inventoryEntryId = managementContextFacade.createInventoryEntry(command);
        if (inventoryEntryId == 0L) {
            throw new IllegalStateException("No se pudo crear la entrada de inventario");
        }
        var created = managementContextFacade.getInventoryEntryById(inventoryEntryId);
        if (created.isEmpty()) {
            throw new InventoryEntryNotFoundException(inventoryEntryId);
        }
        return new ResponseEntity<>(toResource(created.get()), HttpStatus.CREATED);
    }

    @Operation(summary = "Listar entradas del usuario autenticado")
    @GetMapping
    public ResponseEntity<?> getAllInventoryEntries() {
        Optional<Long> userIdOpt = resolveCurrentUserId();
        if (userIdOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }
        var list = managementContextFacade.getInventoryEntriesByUserId(userIdOpt.get());
        return ResponseEntity.ok(list.stream().map(this::toResource).collect(Collectors.toList()));
    }

    @Operation(summary = "Entradas por userId (solo el propio perfil)")
    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getInventoryEntriesByUserId(@PathVariable Long userId) {
        Optional<Long> currentOpt = resolveCurrentUserId();
        if (currentOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }
        if (!currentOpt.get().equals(userId)) {
            return forbidden("No puede consultar entradas de otro perfil");
        }
        var list = managementContextFacade.getInventoryEntriesByUserId(userId);
        return ResponseEntity.ok(list.stream().map(this::toResource).collect(Collectors.toList()));
    }

    @Operation(summary = "Entradas por lote (el lote debe ser suyo)")
    @GetMapping("/coffee-lot/{coffeeLotId}")
    public ResponseEntity<?> getInventoryEntriesByCoffeeLotId(@PathVariable Long coffeeLotId) {
        Optional<Long> currentOpt = resolveCurrentUserId();
        if (currentOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }
        if (!ownsCoffeeLot(coffeeLotId, currentOpt.get())) {
            return forbidden("No autorizado para consultar este lote");
        }
        var list = managementContextFacade.getInventoryEntriesByCoffeeLotId(coffeeLotId);
        return ResponseEntity.ok(list.stream().map(this::toResource).collect(Collectors.toList()));
    }

    @Operation(summary = "Obtener entrada por id")
    @GetMapping("/{inventoryEntryId}")
    public ResponseEntity<?> getInventoryEntryById(@PathVariable Long inventoryEntryId) {
        Optional<Long> currentOpt = resolveCurrentUserId();
        if (currentOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }
        if (!ownsInventoryEntry(inventoryEntryId, currentOpt.get())) {
            return forbidden("No autorizado para ver esta entrada");
        }
        var entry = managementContextFacade.getInventoryEntryById(inventoryEntryId);
        if (entry.isEmpty()) {
            throw new InventoryEntryNotFoundException(inventoryEntryId);
        }
        return ResponseEntity.ok(toResource(entry.get()));
    }

    @Operation(summary = "Actualizar entrada (reajusta stock de lotes)")
    @PutMapping(value = "/{inventoryEntryId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateInventoryEntry(
            @PathVariable Long inventoryEntryId, @Valid @RequestBody UpdateInventoryEntryResource resource) {
        Optional<Long> currentOpt = resolveCurrentUserId();
        if (currentOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }
        if (!ownsInventoryEntry(inventoryEntryId, currentOpt.get())) {
            return forbidden("No autorizado para modificar esta entrada");
        }
        if (!ownsCoffeeLot(resource.coffeeLotId(), currentOpt.get())) {
            return forbidden("Debe vincular la entrada a un lote de su cuenta");
        }
        var command =
                UpdateInventoryEntryCommandFromResourceAssembler.toCommandFromResource(
                        inventoryEntryId, resource);
        var updatedId =
                managementContextFacade.updateInventoryEntry(
                        currentOpt.get(),
                        command.inventoryEntryId(),
                        command.coffeeLotId(),
                        command.quantityUsed(),
                        command.dateUsed(),
                        command.finalProduct());
        if (updatedId == 0L) {
            throw new InventoryEntryNotFoundException(inventoryEntryId);
        }
        var updated = managementContextFacade.getInventoryEntryById(updatedId);
        if (updated.isEmpty()) {
            throw new InventoryEntryNotFoundException(updatedId);
        }
        return ResponseEntity.ok(toResource(updated.get()));
    }

    @Operation(summary = "Eliminar entrada (restaura stock al lote)")
    @DeleteMapping("/{inventoryEntryId}")
    public ResponseEntity<?> deleteInventoryEntry(@PathVariable Long inventoryEntryId) {
        Optional<Long> currentOpt = resolveCurrentUserId();
        if (currentOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }
        if (!ownsInventoryEntry(inventoryEntryId, currentOpt.get())) {
            return forbidden("No autorizado para eliminar esta entrada");
        }
        boolean deleted =
                managementContextFacade.deleteInventoryEntry(currentOpt.get(), inventoryEntryId);
        if (deleted) {
            return ResponseEntity.ok(new MessageResource("Entrada de inventario eliminada exitosamente"));
        }
        throw new InventoryEntryNotFoundException(inventoryEntryId);
    }
}
