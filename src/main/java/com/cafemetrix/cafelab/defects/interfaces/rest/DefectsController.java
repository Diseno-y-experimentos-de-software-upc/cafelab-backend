package com.cafemetrix.cafelab.defects.interfaces.rest;

import com.cafemetrix.cafelab.defects.domain.exceptions.DefectNotFoundException;
import com.cafemetrix.cafelab.defects.domain.model.queries.GetDefectByIdForUserQuery;
import com.cafemetrix.cafelab.defects.domain.model.queries.GetDefectsByUserIdQuery;
import com.cafemetrix.cafelab.defects.domain.services.DefectCommandService;
import com.cafemetrix.cafelab.defects.domain.services.DefectQueryService;
import com.cafemetrix.cafelab.defects.interfaces.rest.resources.CreateDefectResource;
import com.cafemetrix.cafelab.defects.interfaces.rest.resources.DefectResource;
import com.cafemetrix.cafelab.defects.domain.model.commands.DeleteDefectCommand;
import com.cafemetrix.cafelab.defects.interfaces.rest.transform.CreateDefectCommandFromResourceAssembler;
import com.cafemetrix.cafelab.defects.interfaces.rest.transform.DefectResourceFromEntityAssembler;
import com.cafemetrix.cafelab.defects.interfaces.rest.transform.UpdateDefectCommandFromResourceAssembler;
import com.cafemetrix.cafelab.iam.infrastructure.authorization.sfs.support.CurrentProfileIdResolver;
import com.cafemetrix.cafelab.shared.interfaces.rest.resources.MessageResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/defects", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Defects", description = "Registro de defectos (snapshot de café + defecto)")
public class DefectsController {
    private final DefectCommandService defectCommandService;
    private final DefectQueryService defectQueryService;
    private final CurrentProfileIdResolver currentProfileIdResolver;

    public DefectsController(
            DefectCommandService defectCommandService,
            DefectQueryService defectQueryService,
            CurrentProfileIdResolver currentProfileIdResolver) {
        this.defectCommandService = defectCommandService;
        this.defectQueryService = defectQueryService;
        this.currentProfileIdResolver = currentProfileIdResolver;
    }

    private Optional<Long> resolveCurrentUserId() {
        return currentProfileIdResolver.resolveProfileId();
    }

    private ResponseEntity<?> unauthorized(String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResource(message));
    }

    private ResponseEntity<?> forbidden(String message) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResource(message));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Crear registro de defecto (userId desde JWT)")
    public ResponseEntity<?> createDefect(@Valid @RequestBody CreateDefectResource resource) {
        Optional<Long> userIdOpt = resolveCurrentUserId();
        if (userIdOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }
        try {
            var command = CreateDefectCommandFromResourceAssembler.toCommandFromResource(userIdOpt.get(), resource);
            var defect = defectCommandService.handle(command);
            if (defect.isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResource("No se pudo crear el defecto"));
            }
            var defectResource = DefectResourceFromEntityAssembler.toResourceFromEntity(defect.get());
            return new ResponseEntity<>(defectResource, HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new MessageResource(ex.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Listar defectos del perfil autenticado")
    public ResponseEntity<?> getDefectsForCurrentProfile() {
        Optional<Long> userIdOpt = resolveCurrentUserId();
        if (userIdOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }
        var defects = defectQueryService.handle(new GetDefectsByUserIdQuery(userIdOpt.get()));
        var resources = defects.stream()
                .map(DefectResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{defectId}")
    @Operation(summary = "Obtener defecto por id (solo si pertenece al perfil)")
    public ResponseEntity<?> getDefectById(@PathVariable Long defectId) {
        Optional<Long> userIdOpt = resolveCurrentUserId();
        if (userIdOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }
        var defect = defectQueryService.handle(new GetDefectByIdForUserQuery(defectId, userIdOpt.get()));
        if (defect.isEmpty()) {
            throw new DefectNotFoundException(defectId);
        }
        return ResponseEntity.ok(DefectResourceFromEntityAssembler.toResourceFromEntity(defect.get()));
    }

    @PutMapping(value = "/{defectId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Actualizar registro de defecto (solo del perfil autenticado)")
    public ResponseEntity<?> updateDefect(
            @PathVariable Long defectId, @Valid @RequestBody CreateDefectResource resource) {
        Optional<Long> userIdOpt = resolveCurrentUserId();
        if (userIdOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }
        try {
            var command =
                    UpdateDefectCommandFromResourceAssembler.toCommand(
                            defectId, userIdOpt.get(), resource);
            var defect = defectCommandService.handle(command);
            if (defect.isEmpty()) {
                throw new DefectNotFoundException(defectId);
            }
            return ResponseEntity.ok(DefectResourceFromEntityAssembler.toResourceFromEntity(defect.get()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new MessageResource(ex.getMessage()));
        }
    }

    @DeleteMapping("/{defectId}")
    @Operation(summary = "Eliminar registro de defecto (solo del perfil autenticado)")
    public ResponseEntity<Void> deleteDefect(@PathVariable Long defectId) {
        Optional<Long> userIdOpt = resolveCurrentUserId();
        if (userIdOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        boolean removed =
                defectCommandService.handle(new DeleteDefectCommand(defectId, userIdOpt.get()));
        if (!removed) {
            throw new DefectNotFoundException(defectId);
        }
        return ResponseEntity.noContent().build();
    }
}
