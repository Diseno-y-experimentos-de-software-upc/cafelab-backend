package com.cafemetrix.cafelab.production.interfaces.rest;

import com.cafemetrix.cafelab.production.domain.exceptions.SupplierNotFoundException;
import com.cafemetrix.cafelab.production.interfaces.acl.CoffeeproductionContextFacade;
import com.cafemetrix.cafelab.production.interfaces.rest.resources.CreateSupplierResource;
import com.cafemetrix.cafelab.production.interfaces.rest.resources.SupplierResource;
import com.cafemetrix.cafelab.production.interfaces.rest.resources.UpdateSupplierResource;
import com.cafemetrix.cafelab.production.interfaces.rest.transform.UpdateSupplierCommandFromResourceAssembler;
import com.cafemetrix.cafelab.iam.infrastructure.authorization.sfs.support.CurrentProfileIdResolver;
import com.cafemetrix.cafelab.shared.interfaces.rest.resources.MessageResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/suppliers", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Suppliers", description = "Supplier Management Endpoints")
public class SuppliersController {
    private final CoffeeproductionContextFacade coffeeproductionContextFacade;
    private final CurrentProfileIdResolver currentProfileIdResolver;

    public SuppliersController(
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

    private List<SupplierResource> toResources(List<com.cafemetrix.cafelab.production.domain.model.aggregates.Supplier> suppliers) {
        return suppliers.stream()
                .map(supplier -> new SupplierResource(
                        supplier.getId(),
                        supplier.getUserId(),
                        supplier.getName(),
                        supplier.getEmail(),
                        supplier.getPhone(),
                        supplier.getLocation(),
                        supplier.getSpecialties(),
                        supplier.getContactPerson(),
                        supplier.getWebLink()))
                .collect(Collectors.toList());
    }

    private boolean ownsSupplier(Long supplierId, Long currentUserId) {
        return coffeeproductionContextFacade
                .getSupplierById(supplierId)
                .map(s -> s.getUserId().equals(currentUserId))
                .orElse(false);
    }

    @Operation(summary = "Create a new supplier")
    @PostMapping
    public ResponseEntity<?> createSupplier(@Valid @RequestBody CreateSupplierResource resource) {
        Optional<Long> ownerIdOpt = resolveCurrentUserId();
        if (ownerIdOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResource("Usuario no autenticado o perfil no encontrado"));
        }
        Long ownerId = ownerIdOpt.get();
        var supplierId = coffeeproductionContextFacade.createSupplier(
                ownerId, resource.name(), resource.email(),
                resource.phone(), resource.location(), resource.specialties(),
                resource.contactPerson(), resource.webLink());

        if (supplierId == 0L) {
            return ResponseEntity.badRequest()
                    .body(new MessageResource("No se pudo crear el proveedor"));
        }

        var suppliers = coffeeproductionContextFacade.getAllSuppliers();
        var supplier = suppliers.stream()
                .filter(s -> s.getId().equals(supplierId))
                .findFirst();

        if (supplier.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new MessageResource("No se pudo obtener el proveedor creado"));
        }

        var supplierResource = new SupplierResource(
                supplier.get().getId(),
                supplier.get().getUserId(),
                supplier.get().getName(),
                supplier.get().getEmail(),
                supplier.get().getPhone(),
                supplier.get().getLocation(),
                supplier.get().getSpecialties(),
                supplier.get().getContactPerson(),
                supplier.get().getWebLink());

        return new ResponseEntity<>(supplierResource, HttpStatus.CREATED);
    }

    @Operation(summary = "Get suppliers for the authenticated user (no lista global)")
    @GetMapping
    public ResponseEntity<?> getAllSuppliers() {
        Optional<Long> userIdOpt = resolveCurrentUserId();
        if (userIdOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResource("Usuario no autenticado o perfil no encontrado"));
        }
        var suppliers = coffeeproductionContextFacade.getSuppliersByUserId(userIdOpt.get());
        return ResponseEntity.ok(toResources(suppliers));
    }

    @Operation(summary = "Proveedores por userId (solo el propio perfil)")
    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getSuppliersByUserId(@PathVariable Long userId) {
        Optional<Long> currentOpt = resolveCurrentUserId();
        if (currentOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResource("Usuario no autenticado o perfil no encontrado"));
        }
        if (!currentOpt.get().equals(userId)) {
            return forbidden("No puede consultar proveedores de otro perfil");
        }
        var suppliers = coffeeproductionContextFacade.getSuppliersByUserId(userId);
        return ResponseEntity.ok(toResources(suppliers));
    }

    @Operation(summary = "Get supplier by ID")
    @GetMapping("/{supplierId}")
    public ResponseEntity<?> getSupplierById(@PathVariable Long supplierId) {
        Optional<Long> currentOpt = resolveCurrentUserId();
        if (currentOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResource("Usuario no autenticado o perfil no encontrado"));
        }
        var supplier = coffeeproductionContextFacade.getSupplierById(supplierId);

        if (supplier.isEmpty()) {
            throw new SupplierNotFoundException(supplierId);
        }
        if (!supplier.get().getUserId().equals(currentOpt.get())) {
            return forbidden("No autorizado para ver este proveedor");
        }

        var supplierResource = new SupplierResource(
                supplier.get().getId(),
                supplier.get().getUserId(),
                supplier.get().getName(),
                supplier.get().getEmail(),
                supplier.get().getPhone(),
                supplier.get().getLocation(),
                supplier.get().getSpecialties(),
                supplier.get().getContactPerson(),
                supplier.get().getWebLink());

        return ResponseEntity.ok(supplierResource);
    }

    @Operation(summary = "Update a supplier")
    @PutMapping("/{supplierId}")
    public ResponseEntity<?> updateSupplier(
            @PathVariable Long supplierId,
            @Valid @RequestBody UpdateSupplierResource resource) {

        Optional<Long> currentOpt = resolveCurrentUserId();
        if (currentOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResource("Usuario no autenticado o perfil no encontrado"));
        }
        if (!ownsSupplier(supplierId, currentOpt.get())) {
            return forbidden("No autorizado para modificar este proveedor");
        }

        var updateSupplierCommand = UpdateSupplierCommandFromResourceAssembler.toCommandFromResource(supplierId, resource);
        var updatedSupplierId = coffeeproductionContextFacade.updateSupplier(
                updateSupplierCommand.supplierId(),
                updateSupplierCommand.name(),
                updateSupplierCommand.email(),
                updateSupplierCommand.phone(),
                updateSupplierCommand.location(),
                updateSupplierCommand.specialties(),
                updateSupplierCommand.contactPerson(),
                updateSupplierCommand.webLink());

        if (updatedSupplierId == 0L) {
            throw new SupplierNotFoundException(supplierId);
        }

        var suppliers = coffeeproductionContextFacade.getAllSuppliers();
        var supplier = suppliers.stream()
                .filter(s -> s.getId().equals(updatedSupplierId))
                .findFirst();

        if (supplier.isEmpty()) {
            throw new SupplierNotFoundException(supplierId);
        }

        var supplierResource = new SupplierResource(
                supplier.get().getId(),
                supplier.get().getUserId(),
                supplier.get().getName(),
                supplier.get().getEmail(),
                supplier.get().getPhone(),
                supplier.get().getLocation(),
                supplier.get().getSpecialties(),
                supplier.get().getContactPerson(),
                supplier.get().getWebLink());

        return ResponseEntity.ok(supplierResource);
    }

    @Operation(summary = "Delete a supplier")
    @DeleteMapping("/{supplierId}")
    public ResponseEntity<?> deleteSupplier(@PathVariable Long supplierId) {
        Optional<Long> currentOpt = resolveCurrentUserId();
        if (currentOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResource("Usuario no autenticado o perfil no encontrado"));
        }
        if (!ownsSupplier(supplierId, currentOpt.get())) {
            return forbidden("No autorizado para eliminar este proveedor");
        }

        var deleted = coffeeproductionContextFacade.deleteSupplier(supplierId);
        if (deleted) {
            return ResponseEntity.ok(new MessageResource("Proveedor eliminado exitosamente"));
        }
        throw new SupplierNotFoundException(supplierId);
    }
}
