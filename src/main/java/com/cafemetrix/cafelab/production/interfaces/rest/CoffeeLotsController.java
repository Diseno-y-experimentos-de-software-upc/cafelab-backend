package com.cafemetrix.cafelab.production.interfaces.rest;

import com.cafemetrix.cafelab.production.domain.exceptions.CoffeeLotNotFoundException;
import com.cafemetrix.cafelab.production.domain.model.aggregates.CoffeeLot;
import com.cafemetrix.cafelab.production.interfaces.acl.CoffeeproductionContextFacade;
import com.cafemetrix.cafelab.production.interfaces.rest.resources.CoffeeLotResource;
import com.cafemetrix.cafelab.production.interfaces.rest.resources.CreateCoffeeLotResource;
import com.cafemetrix.cafelab.production.interfaces.rest.resources.AnnullCoffeeLotResource;
import com.cafemetrix.cafelab.production.interfaces.rest.resources.UpdateCoffeeLotResource;
import com.cafemetrix.cafelab.production.interfaces.rest.transform.CreateCoffeeLotVersionCommandFromResourceAssembler;
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
@RequestMapping(value = "/api/v1/coffee-lots", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Coffee Lots", description = "Coffee Lot Management Endpoints")
public class CoffeeLotsController {
    private final CoffeeproductionContextFacade coffeeproductionContextFacade;
    private final CurrentProfileIdResolver currentProfileIdResolver;

    public CoffeeLotsController(
            CoffeeproductionContextFacade coffeeproductionContextFacade,
            CurrentProfileIdResolver currentProfileIdResolver) {
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

    private CoffeeLotResource toResource(CoffeeLot lot) {
        return new CoffeeLotResource(
                lot.getId(),
                lot.getUserId(),
                lot.getSupplierId(),
                lot.getSupplierName(),
                lot.getLotLineageId(),
                lot.getVersionNumber(),
                lot.getIsCurrent(),
                lot.getSupersedesId(),
                lot.getRecordStatus(),
                lot.getAnnulmentReason(),
                lot.getLotName(),
                lot.getCoffeeType(),
                lot.getProcessingMethod(),
                lot.getAltitude(),
                lot.getWeight(),
                lot.getOriginalWeight(),
                lot.getOrigin(),
                lot.getStatus(),
                lot.getCertifications());
    }

    private List<CoffeeLotResource> toResources(List<CoffeeLot> lots) {
        return lots.stream().map(this::toResource).collect(Collectors.toList());
    }

    private boolean ownsSupplier(Long supplierId, Long currentUserId) {
        return coffeeproductionContextFacade
                .getSupplierById(supplierId)
                .map(s -> s.getUserId().equals(currentUserId))
                .orElse(false);
    }

    private boolean ownsCoffeeLot(Long coffeeLotId, Long currentUserId) {
        return coffeeproductionContextFacade
                .getCoffeeLotById(coffeeLotId)
                .map(l -> l.getUserId().equals(currentUserId))
                .orElse(false);
    }

    @Operation(summary = "Crear lote (perfil desde JWT; proveedor debe ser suyo)")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createCoffeeLot(@Valid @RequestBody CreateCoffeeLotResource resource) {
        Optional<Long> ownerIdOpt = resolveCurrentUserId();
        if (ownerIdOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }
        Long ownerId = ownerIdOpt.get();
        if (!ownsSupplier(resource.supplier_id(), ownerId)) {
            return forbidden("Debe vincular el lote a un proveedor de su cuenta");
        }

        var coffeeLotId = coffeeproductionContextFacade.createCoffeeLot(
                ownerId,
                resource.supplier_id(),
                resource.lot_name(),
                resource.coffee_type(),
                resource.processing_method(),
                resource.altitude(),
                resource.weight(),
                resource.origin(),
                resource.status(),
                resource.certifications());

        if (coffeeLotId == 0L) {
            return ResponseEntity.badRequest()
                    .body(new MessageResource("No se pudo crear el lote de café"));
        }

        var coffeeLots = coffeeproductionContextFacade.getAllCoffeeLots();
        var coffeeLot = coffeeLots.stream()
                .filter(lot -> lot.getId().equals(coffeeLotId))
                .findFirst();

        if (coffeeLot.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new MessageResource("No se pudo obtener el lote de café creado"));
        }

        return new ResponseEntity<>(toResource(coffeeLot.get()), HttpStatus.CREATED);
    }

    @Operation(summary = "Listar lotes del usuario autenticado")
    @GetMapping
    public ResponseEntity<?> getAllCoffeeLots() {
        Optional<Long> userIdOpt = resolveCurrentUserId();
        if (userIdOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }
        var coffeeLots = coffeeproductionContextFacade.getCoffeeLotsByUserId(userIdOpt.get());
        return ResponseEntity.ok(toResources(coffeeLots));
    }

    @Operation(summary = "Listar lotes activos seleccionables (inventario, costos)")
    @GetMapping("/selectable")
    public ResponseEntity<?> getSelectableCoffeeLots() {
        Optional<Long> userIdOpt = resolveCurrentUserId();
        if (userIdOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }
        var coffeeLots = coffeeproductionContextFacade.getSelectableCoffeeLots().stream()
                .filter(lot -> lot.getUserId().equals(userIdOpt.get()))
                .toList();
        return ResponseEntity.ok(toResources(coffeeLots));
    }

    @Operation(summary = "Lotes por userId (solo el propio perfil)")
    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getCoffeeLotsByUserId(@PathVariable Long userId) {
        Optional<Long> currentOpt = resolveCurrentUserId();
        if (currentOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }
        if (!currentOpt.get().equals(userId)) {
            return forbidden("No puede consultar lotes de otro perfil");
        }
        var coffeeLots = coffeeproductionContextFacade.getCoffeeLotsByUserId(userId);
        return ResponseEntity.ok(toResources(coffeeLots));
    }

    @Operation(summary = "Get coffee lots by supplier ID (proveedor debe ser suyo)")
    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<?> getCoffeeLotsBySupplierId(@PathVariable Long supplierId) {
        Optional<Long> currentOpt = resolveCurrentUserId();
        if (currentOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }
        if (!ownsSupplier(supplierId, currentOpt.get())) {
            return forbidden("No autorizado para consultar lotes de este proveedor");
        }
        var coffeeLots = coffeeproductionContextFacade.getCoffeeLotsBySupplierId(supplierId);
        return ResponseEntity.ok(toResources(coffeeLots));
    }

    @Operation(summary = "Get coffee lot by ID")
    @GetMapping("/{coffeeLotId}")
    public ResponseEntity<?> getCoffeeLotById(@PathVariable Long coffeeLotId) {
        Optional<Long> currentOpt = resolveCurrentUserId();
        if (currentOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }
        var coffeeLot = coffeeproductionContextFacade.getCoffeeLotById(coffeeLotId);

        if (coffeeLot.isEmpty()) {
            throw new CoffeeLotNotFoundException(coffeeLotId);
        }
        if (!coffeeLot.get().getUserId().equals(currentOpt.get())) {
            return forbidden("No autorizado para ver este lote");
        }

        return ResponseEntity.ok(toResource(coffeeLot.get()));
    }

    @Operation(summary = "Create a new version of a coffee lot")
    @PostMapping(value = "/{coffeeLotId}/versions", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createCoffeeLotVersion(
            @PathVariable Long coffeeLotId,
            @Valid @RequestBody UpdateCoffeeLotResource resource) {

        Optional<Long> currentOpt = resolveCurrentUserId();
        if (currentOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }
        if (!ownsCoffeeLot(coffeeLotId, currentOpt.get())) {
            return forbidden("No autorizado para modificar este lote");
        }

        var createVersionCommand = CreateCoffeeLotVersionCommandFromResourceAssembler.toCommandFromResource(coffeeLotId, resource);
        var newCoffeeLotId = coffeeproductionContextFacade.createCoffeeLotVersion(
                createVersionCommand.coffeeLotId(),
                createVersionCommand.lotName(),
                createVersionCommand.coffeeType(),
                createVersionCommand.processingMethod(),
                createVersionCommand.altitude(),
                createVersionCommand.origin(),
                createVersionCommand.status(),
                createVersionCommand.certifications());

        if (newCoffeeLotId == 0L) {
            throw new CoffeeLotNotFoundException(coffeeLotId);
        }

        var coffeeLot = coffeeproductionContextFacade.getCoffeeLotById(newCoffeeLotId);
        if (coffeeLot.isEmpty()) {
            throw new CoffeeLotNotFoundException(coffeeLotId);
        }

        return ResponseEntity.ok(toResource(coffeeLot.get()));
    }

    @Operation(summary = "Get version history for a coffee lot lineage")
    @GetMapping("/lineage/{lineageId}/versions")
    public ResponseEntity<?> getCoffeeLotVersionsByLineage(@PathVariable Long lineageId) {
        Optional<Long> currentOpt = resolveCurrentUserId();
        if (currentOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }

        var versions = coffeeproductionContextFacade.getCoffeeLotVersionsByLineageId(lineageId);
        if (versions.isEmpty()) {
            throw new CoffeeLotNotFoundException(lineageId);
        }
        if (!versions.get(0).getUserId().equals(currentOpt.get())) {
            return forbidden("No autorizado para consultar este lote");
        }

        return ResponseEntity.ok(toResources(versions));
    }

    @Operation(summary = "Anular lote (no lo borra: queda para auditoría)")
    @PostMapping(value = "/{coffeeLotId}/annulment", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> annullCoffeeLot(
            @PathVariable Long coffeeLotId,
            @Valid @RequestBody AnnullCoffeeLotResource resource) {

        Optional<Long> currentOpt = resolveCurrentUserId();
        if (currentOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }
        if (!ownsCoffeeLot(coffeeLotId, currentOpt.get())) {
            return forbidden("No autorizado para anular este lote");
        }

        var existing = coffeeproductionContextFacade.getCoffeeLotById(coffeeLotId);
        if (existing.isEmpty()) {
            throw new CoffeeLotNotFoundException(coffeeLotId);
        }
        if (existing.get().isAnnulled()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MessageResource("El lote ya está anulado"));
        }

        var annulledId = coffeeproductionContextFacade.annullCoffeeLot(coffeeLotId, resource.reason());
        if (annulledId == 0L) {
            throw new CoffeeLotNotFoundException(coffeeLotId);
        }

        var annulled = coffeeproductionContextFacade.getCoffeeLotById(annulledId);
        if (annulled.isEmpty()) {
            throw new CoffeeLotNotFoundException(coffeeLotId);
        }

        return ResponseEntity.ok(toResource(annulled.get()));
    }
}
