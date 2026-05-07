package com.cafemetrix.cafelab.management.interfaces.rest;

import com.cafemetrix.cafelab.management.domain.exceptions.ProductionCostRecordNotFoundException;
import com.cafemetrix.cafelab.management.domain.model.aggregates.ProductionCostRecord;
import com.cafemetrix.cafelab.management.domain.model.commands.CreateProductionCostRecordCommand;
import com.cafemetrix.cafelab.management.domain.model.commands.UpdateProductionCostRecordCommand;
import com.cafemetrix.cafelab.management.domain.model.support.ProductionCostTotalsCalculator;
import com.cafemetrix.cafelab.management.interfaces.acl.ManagementContextFacade;
import com.cafemetrix.cafelab.management.interfaces.rest.resources.AnnullProductionCostRecordResource;
import com.cafemetrix.cafelab.management.interfaces.rest.resources.CreateProductionCostRecordResource;
import com.cafemetrix.cafelab.management.interfaces.rest.resources.ProductionCostRecordResource;
import com.cafemetrix.cafelab.management.interfaces.rest.resources.UpdateProductionCostRecordResource;
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

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/production-cost-records", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Production Cost Records", description = "Registros de gestión de costos de producción")
public class ProductionCostRecordsController {
    private final ManagementContextFacade managementContextFacade;
    private final CoffeeproductionContextFacade coffeeproductionContextFacade;
    private final CurrentProfileIdResolver currentProfileIdResolver;

    public ProductionCostRecordsController(
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

    private boolean ownsProductionCostRecord(Long recordId, Long currentUserId) {
        return managementContextFacade
                .getProductionCostRecordByIdAndUserId(recordId, currentUserId)
                .isPresent();
    }

    private static String normalizeCurrency(String currency) {
        if (currency == null || currency.isBlank()) {
            return "PEN";
        }
        return "USD".equalsIgnoreCase(currency.trim()) ? "USD" : "PEN";
    }

    private static double marginOrDefault(Double marginPercent) {
        return marginPercent != null ? marginPercent : 45.0;
    }

    private static String formatIso(Date d) {
        if (d == null) {
            return null;
        }
        return Instant.ofEpochMilli(d.getTime()).toString();
    }

    private ProductionCostRecordResource toResource(ProductionCostRecord e) {
        return new ProductionCostRecordResource(
                e.getId(),
                e.getUserId(),
                e.getCoffeeLotId(),
                e.getLotName(),
                e.getCoffeeType(),
                e.getCurrency(),
                e.getTotalKg(),
                e.getMarginPercent(),
                e.getRawMaterialsCost(),
                e.getLaborCost(),
                e.getTransportCost(),
                e.getStorageCost(),
                e.getProcessingCost(),
                e.getOtherIndirectCosts(),
                e.getTotalDirectCost(),
                e.getTotalIndirectCost(),
                e.getTotalCost(),
                e.getCostPerKg(),
                e.getSuggestedPrice(),
                e.getPotentialMargin(),
                e.getStatus(),
                e.getReason(),
                formatIso(e.getCreatedAt()),
                formatIso(e.getUpdatedAt()));
    }

    private CreateProductionCostRecordCommand buildCreateCommand(
            Long ownerId,
            com.cafemetrix.cafelab.production.domain.model.aggregates.CoffeeLot lot,
            CreateProductionCostRecordResource resource) {
        String currency = normalizeCurrency(resource.currency());
        double margin = marginOrDefault(resource.marginPercent());
        var totals =
                ProductionCostTotalsCalculator.compute(
                        resource.totalKg(),
                        margin,
                        resource.rawMaterialsCost(),
                        resource.laborCost(),
                        resource.transportCost(),
                        resource.storageCost(),
                        resource.processingCost(),
                        resource.otherIndirectCosts());
        return new CreateProductionCostRecordCommand(
                ownerId,
                lot.getId(),
                lot.getLotName(),
                lot.getCoffeeType(),
                currency,
                resource.totalKg(),
                margin,
                resource.rawMaterialsCost(),
                resource.laborCost(),
                resource.transportCost(),
                resource.storageCost(),
                resource.processingCost(),
                resource.otherIndirectCosts(),
                totals.totalDirectCost(),
                totals.totalIndirectCost(),
                totals.totalCost(),
                totals.costPerKg(),
                totals.suggestedPrice(),
                totals.potentialMargin());
    }

    private UpdateProductionCostRecordCommand buildUpdateCommand(
            Long recordId,
            com.cafemetrix.cafelab.production.domain.model.aggregates.CoffeeLot lot,
            UpdateProductionCostRecordResource resource) {
        String currency = normalizeCurrency(resource.currency());
        double margin = marginOrDefault(resource.marginPercent());
        var totals =
                ProductionCostTotalsCalculator.compute(
                        resource.totalKg(),
                        margin,
                        resource.rawMaterialsCost(),
                        resource.laborCost(),
                        resource.transportCost(),
                        resource.storageCost(),
                        resource.processingCost(),
                        resource.otherIndirectCosts());
        return new UpdateProductionCostRecordCommand(
                recordId,
                lot.getId(),
                lot.getLotName(),
                lot.getCoffeeType(),
                currency,
                resource.totalKg(),
                margin,
                resource.rawMaterialsCost(),
                resource.laborCost(),
                resource.transportCost(),
                resource.storageCost(),
                resource.processingCost(),
                resource.otherIndirectCosts(),
                totals.totalDirectCost(),
                totals.totalIndirectCost(),
                totals.totalCost(),
                totals.costPerKg(),
                totals.suggestedPrice(),
                totals.potentialMargin());
    }

    @Operation(summary = "Crear registro de costo de producción")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@Valid @RequestBody CreateProductionCostRecordResource resource) {
        Optional<Long> ownerOpt = resolveCurrentUserId();
        if (ownerOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }
        Long ownerId = ownerOpt.get();
        if (!ownsCoffeeLot(resource.coffeeLotId(), ownerId)) {
            return forbidden("Debe usar un lote de su cuenta");
        }
        var lotOpt = coffeeproductionContextFacade.getCoffeeLotById(resource.coffeeLotId());
        if (lotOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResource("Lote no encontrado"));
        }
        var command = buildCreateCommand(ownerId, lotOpt.get(), resource);
        Long id = managementContextFacade.createProductionCostRecord(command);
        if (id == null || id == 0L) {
            return ResponseEntity.badRequest().body(new MessageResource("No se pudo crear el registro"));
        }
        var created =
                managementContextFacade
                        .getProductionCostRecordByIdAndUserId(id, ownerId)
                        .orElseThrow(() -> new ProductionCostRecordNotFoundException(id));
        return new ResponseEntity<>(toResource(created), HttpStatus.CREATED);
    }

    @Operation(summary = "Listar registros del usuario autenticado")
    @GetMapping
    public ResponseEntity<?> listMine() {
        Optional<Long> userIdOpt = resolveCurrentUserId();
        if (userIdOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }
        List<ProductionCostRecord> list =
                managementContextFacade.getProductionCostRecordsByUserId(userIdOpt.get());
        return ResponseEntity.ok(list.stream().map(this::toResource).collect(Collectors.toList()));
    }

    @Operation(summary = "Obtener registro por id")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<Long> currentOpt = resolveCurrentUserId();
        if (currentOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }
        if (!ownsProductionCostRecord(id, currentOpt.get())) {
            return forbidden("No autorizado para ver este registro");
        }
        var row =
                managementContextFacade
                        .getProductionCostRecordById(id)
                        .orElseThrow(() -> new ProductionCostRecordNotFoundException(id));
        return ResponseEntity.ok(toResource(row));
    }

    @Operation(summary = "Actualizar registro")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(
            @PathVariable Long id, @Valid @RequestBody UpdateProductionCostRecordResource resource) {
        Optional<Long> currentOpt = resolveCurrentUserId();
        if (currentOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }
        if (!ownsProductionCostRecord(id, currentOpt.get())) {
            return forbidden("No autorizado para modificar este registro");
        }
        // Un registro anulado queda como evidencia de auditoría: no se puede modificar más.
        var existing =
                managementContextFacade
                        .getProductionCostRecordByIdAndUserId(id, currentOpt.get())
                        .orElseThrow(() -> new ProductionCostRecordNotFoundException(id));
        if (existing.isAnnulled()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MessageResource("No se puede modificar un registro anulado"));
        }
        if (!ownsCoffeeLot(resource.coffeeLotId(), currentOpt.get())) {
            return forbidden("Debe usar un lote de su cuenta");
        }
        var lotOpt = coffeeproductionContextFacade.getCoffeeLotById(resource.coffeeLotId());
        if (lotOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResource("Lote no encontrado"));
        }
        var command = buildUpdateCommand(id, lotOpt.get(), resource);
        Long updatedId = managementContextFacade.updateProductionCostRecord(currentOpt.get(), command);
        if (updatedId == null || updatedId == 0L) {
            throw new ProductionCostRecordNotFoundException(id);
        }
        var updated =
                managementContextFacade
                        .getProductionCostRecordByIdAndUserId(updatedId, currentOpt.get())
                        .orElseThrow(() -> new ProductionCostRecordNotFoundException(id));
        return ResponseEntity.ok(toResource(updated));
    }

    @Operation(summary = "Anular registro (no lo borra: queda para auditoría)")
    @PostMapping(value = "/{id}/annulment", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> annull(
            @PathVariable Long id,
            @Valid @RequestBody AnnullProductionCostRecordResource resource) {
        Optional<Long> currentOpt = resolveCurrentUserId();
        if (currentOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }
        if (!ownsProductionCostRecord(id, currentOpt.get())) {
            return forbidden("No autorizado para anular este registro");
        }
        var existing =
                managementContextFacade
                        .getProductionCostRecordByIdAndUserId(id, currentOpt.get())
                        .orElseThrow(() -> new ProductionCostRecordNotFoundException(id));
        if (existing.isAnnulled()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MessageResource("El registro ya está anulado"));
        }
        Long annulledId =
                managementContextFacade.annullProductionCostRecord(
                        currentOpt.get(), id, resource.reason());
        if (annulledId == null || annulledId == 0L) {
            throw new ProductionCostRecordNotFoundException(id);
        }
        var annulled =
                managementContextFacade
                        .getProductionCostRecordByIdAndUserId(annulledId, currentOpt.get())
                        .orElseThrow(() -> new ProductionCostRecordNotFoundException(id));
        return ResponseEntity.ok(toResource(annulled));
    }

    @Operation(summary = "Eliminar registro definitivamente (uso interno; el frontend usa anulación)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Optional<Long> currentOpt = resolveCurrentUserId();
        if (currentOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }
        if (!ownsProductionCostRecord(id, currentOpt.get())) {
            return forbidden("No autorizado para eliminar este registro");
        }
        boolean deleted = managementContextFacade.deleteProductionCostRecord(currentOpt.get(), id);
        if (deleted) {
            return ResponseEntity.ok(new MessageResource("Registro de costo eliminado exitosamente"));
        }
        throw new ProductionCostRecordNotFoundException(id);
    }
}
