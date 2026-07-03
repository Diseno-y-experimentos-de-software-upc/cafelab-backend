package com.cafemetrix.cafelab.cuppingsessions.interfaces.rest;

import com.cafemetrix.cafelab.cuppingsessions.domain.exceptions.CuppingSessionNotFoundException;
import com.cafemetrix.cafelab.cuppingsessions.domain.model.queries.GetCuppingSessionByIdForUserQuery;
import com.cafemetrix.cafelab.cuppingsessions.domain.model.queries.GetCuppingSessionsByCoffeeLotIdQuery;
import com.cafemetrix.cafelab.cuppingsessions.domain.model.queries.GetCuppingSessionsByUserIdQuery;
import com.cafemetrix.cafelab.cuppingsessions.domain.services.CuppingSessionCommandService;
import com.cafemetrix.cafelab.cuppingsessions.domain.services.CuppingSessionQueryService;
import com.cafemetrix.cafelab.cuppingsessions.interfaces.rest.resources.CreateCuppingSessionResource;
import com.cafemetrix.cafelab.cuppingsessions.interfaces.rest.resources.CuppingSessionResource;
import com.cafemetrix.cafelab.cuppingsessions.interfaces.rest.resources.UpdateCuppingSessionResource;
import com.cafemetrix.cafelab.cuppingsessions.interfaces.rest.transform.CreateCuppingSessionCommandFromResourceAssembler;
import com.cafemetrix.cafelab.cuppingsessions.interfaces.rest.transform.CuppingSessionResourceFromEntityAssembler;
import com.cafemetrix.cafelab.cuppingsessions.interfaces.rest.transform.UpdateCuppingSessionCommandFromResourceAssembler;
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
@RequestMapping(value = "/api/v1/cupping-sessions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Cupping sessions", description = "Sesiones de cata por perfil")
public class CuppingSessionsController {
    private final CuppingSessionCommandService commandService;
    private final CuppingSessionQueryService queryService;
    private final CurrentProfileIdResolver currentProfileIdResolver;

    public CuppingSessionsController(
            CuppingSessionCommandService commandService,
            CuppingSessionQueryService queryService,
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
    @Operation(summary = "Crear sesión de cata")
    public ResponseEntity<?> create(@RequestBody CreateCuppingSessionResource resource) {
        Optional<Long> userIdOpt = resolveCurrentUserId();
        if (userIdOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }
        try {
            var cmd = CreateCuppingSessionCommandFromResourceAssembler.toCommand(userIdOpt.get(), resource);
            var created = commandService.handle(cmd);
            if (created.isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResource("No se pudo crear la sesión"));
            }
            return new ResponseEntity<>(
                    CuppingSessionResourceFromEntityAssembler.toResource(created.get()), HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new MessageResource(ex.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Listar sesiones del perfil (más recientes primero)")
    public ResponseEntity<?> list() {
        Optional<Long> userIdOpt = resolveCurrentUserId();
        if (userIdOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }
        var list = queryService.handle(new GetCuppingSessionsByUserIdQuery(userIdOpt.get()));
        return ResponseEntity.ok(
                list.stream().map(CuppingSessionResourceFromEntityAssembler::toResource).collect(Collectors.toList()));
    }

    @GetMapping("/lot/{coffeeLotId}")
    @Operation(summary = "Listar sesiones del perfil asociadas a un lote de café")
    public ResponseEntity<?> listByCoffeeLot(@PathVariable Long coffeeLotId) {
        Optional<Long> userIdOpt = resolveCurrentUserId();
        if (userIdOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }
        var list = queryService.handle(new GetCuppingSessionsByCoffeeLotIdQuery(userIdOpt.get(), coffeeLotId));
        return ResponseEntity.ok(
                list.stream().map(CuppingSessionResourceFromEntityAssembler::toResource).collect(Collectors.toList()));
    }

    @GetMapping("/{sessionId}")
    @Operation(summary = "Obtener sesión por id")
    public ResponseEntity<?> getById(@PathVariable Long sessionId) {
        Optional<Long> userIdOpt = resolveCurrentUserId();
        if (userIdOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }
        var found = queryService.handle(new GetCuppingSessionByIdForUserQuery(sessionId, userIdOpt.get()));
        if (found.isEmpty()) {
            throw new CuppingSessionNotFoundException(sessionId);
        }
        return ResponseEntity.ok(CuppingSessionResourceFromEntityAssembler.toResource(found.get()));
    }

    @PutMapping(value = "/{sessionId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Actualizar sesión")
    public ResponseEntity<?> update(
            @PathVariable Long sessionId, @RequestBody UpdateCuppingSessionResource resource) {
        Optional<Long> userIdOpt = resolveCurrentUserId();
        if (userIdOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }
        try {
            var cmd =
                    UpdateCuppingSessionCommandFromResourceAssembler.toCommand(
                            sessionId, userIdOpt.get(), resource);
            var updated = commandService.handle(cmd);
            if (updated.isEmpty()) {
                throw new CuppingSessionNotFoundException(sessionId);
            }
            return ResponseEntity.ok(CuppingSessionResourceFromEntityAssembler.toResource(updated.get()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new MessageResource(ex.getMessage()));
        }
    }

    @DeleteMapping("/{sessionId}")
    @Operation(summary = "Eliminar sesión")
    public ResponseEntity<?> delete(@PathVariable Long sessionId) {
        Optional<Long> userIdOpt = resolveCurrentUserId();
        if (userIdOpt.isEmpty()) {
            return unauthorized("Usuario no autenticado o perfil no encontrado");
        }
        boolean ok = commandService.delete(sessionId, userIdOpt.get());
        if (!ok) {
            throw new CuppingSessionNotFoundException(sessionId);
        }
        return ResponseEntity.noContent().build();
    }
}
