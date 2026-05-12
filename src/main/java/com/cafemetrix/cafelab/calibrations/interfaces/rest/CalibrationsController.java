package com.cafemetrix.cafelab.calibrations.interfaces.rest;

import com.cafemetrix.cafelab.calibrations.domain.exceptions.GrindCalibrationNotFoundException;
import com.cafemetrix.cafelab.calibrations.domain.model.commands.DeleteGrindCalibrationCommand;
import com.cafemetrix.cafelab.calibrations.domain.model.queries.GetGrindCalibrationByIdForUserQuery;
import com.cafemetrix.cafelab.calibrations.domain.model.queries.GetGrindCalibrationsByUserIdQuery;
import com.cafemetrix.cafelab.calibrations.domain.services.GrindCalibrationCommandService;
import com.cafemetrix.cafelab.calibrations.domain.services.GrindCalibrationQueryService;
import com.cafemetrix.cafelab.calibrations.interfaces.rest.resources.CreateGrindCalibrationResource;
import com.cafemetrix.cafelab.calibrations.interfaces.rest.resources.GrindCalibrationResource;
import com.cafemetrix.cafelab.calibrations.interfaces.rest.resources.UpdateGrindCalibrationResource;
import com.cafemetrix.cafelab.calibrations.interfaces.rest.transform.CreateGrindCalibrationCommandFromResourceAssembler;
import com.cafemetrix.cafelab.calibrations.interfaces.rest.transform.GrindCalibrationResourceFromEntityAssembler;
import com.cafemetrix.cafelab.calibrations.interfaces.rest.transform.UpdateGrindCalibrationCommandFromResourceAssembler;
import com.cafemetrix.cafelab.iam.infrastructure.authorization.sfs.support.CurrentProfileIdResolver;
import com.cafemetrix.cafelab.shared.interfaces.rest.resources.MessageResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/calibrations", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Grind calibrations", description = "Registros de calibración de molienda por perfil")
public class CalibrationsController {
    private final GrindCalibrationCommandService commandService;
    private final GrindCalibrationQueryService queryService;
    private final CurrentProfileIdResolver currentProfileIdResolver;

    public CalibrationsController(
            GrindCalibrationCommandService commandService,
            GrindCalibrationQueryService queryService,
            CurrentProfileIdResolver currentProfileIdResolver) {
        this.commandService = commandService;
        this.queryService = queryService;
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
    @Operation(summary = "Crear calibración (userId desde JWT)")
    public ResponseEntity<?> create(@RequestBody CreateGrindCalibrationResource resource) {
        Optional<Long> userIdOpt = resolveCurrentUserId();
        if (userIdOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }
        try {
            var command =
                    CreateGrindCalibrationCommandFromResourceAssembler.toCommand(
                            userIdOpt.get(), resource);
            var created = commandService.handle(command);
            if (created.isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResource("No se pudo crear la calibración"));
            }
            return new ResponseEntity<>(
                    GrindCalibrationResourceFromEntityAssembler.toResourceFromEntity(created.get()),
                    HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new MessageResource(ex.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Listar calibraciones del perfil autenticado")
    public ResponseEntity<?> listForCurrentProfile() {
        Optional<Long> userIdOpt = resolveCurrentUserId();
        if (userIdOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }
        var list =
                queryService.handle(new GetGrindCalibrationsByUserIdQuery(userIdOpt.get()));
        var resources =
                list.stream()
                        .map(GrindCalibrationResourceFromEntityAssembler::toResourceFromEntity)
                        .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{calibrationId}")
    @Operation(summary = "Obtener calibración por id (solo del propio perfil)")
    public ResponseEntity<?> getById(@PathVariable Long calibrationId) {
        Optional<Long> userIdOpt = resolveCurrentUserId();
        if (userIdOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }
        var found =
                queryService.handle(
                        new GetGrindCalibrationByIdForUserQuery(calibrationId, userIdOpt.get()));
        if (found.isEmpty()) {
            throw new GrindCalibrationNotFoundException(calibrationId);
        }
        return ResponseEntity.ok(
                GrindCalibrationResourceFromEntityAssembler.toResourceFromEntity(found.get()));
    }

    @PutMapping(value = "/{calibrationId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Actualizar calibración (solo del propio perfil)")
    public ResponseEntity<?> update(
            @PathVariable Long calibrationId, @RequestBody UpdateGrindCalibrationResource resource) {
        Optional<Long> userIdOpt = resolveCurrentUserId();
        if (userIdOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }
        try {
            var command =
                    UpdateGrindCalibrationCommandFromResourceAssembler.toCommand(
                            calibrationId, userIdOpt.get(), resource);
            var updated = commandService.handle(command);
            if (updated.isEmpty()) {
                throw new GrindCalibrationNotFoundException(calibrationId);
            }
            return ResponseEntity.ok(
                    GrindCalibrationResourceFromEntityAssembler.toResourceFromEntity(updated.get()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new MessageResource(ex.getMessage()));
        }
    }

    @DeleteMapping("/{calibrationId}")
    @Operation(summary = "Eliminar calibración (solo del propio perfil)")
    public ResponseEntity<?> delete(@PathVariable Long calibrationId) {
        Optional<Long> userIdOpt = resolveCurrentUserId();
        if (userIdOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }

        var deleted =
                commandService.handle(
                        new DeleteGrindCalibrationCommand(calibrationId, userIdOpt.get()));
        if (deleted) {
            return ResponseEntity.ok(new MessageResource("Calibración eliminada exitosamente"));
        }
        throw new GrindCalibrationNotFoundException(calibrationId);
    }
}
