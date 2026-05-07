package com.cafemetrix.cafelab.iam.interfaces.rest;

import com.cafemetrix.cafelab.iam.domain.exceptions.InvalidCurrentPasswordException;
import com.cafemetrix.cafelab.iam.domain.model.commands.ChangePasswordCommand;
import com.cafemetrix.cafelab.iam.domain.services.UserCommandService;
import com.cafemetrix.cafelab.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.cafemetrix.cafelab.iam.interfaces.rest.resources.ChangePasswordResource;
import com.cafemetrix.cafelab.shared.interfaces.rest.resources.MessageResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Operaciones sobre la cuenta del usuario autenticado. Vive bajo {@code /api/v1/account/**} y, al
 * no estar listado en {@code permitAll()} de {@code WebSecurityConfiguration}, exige JWT.
 */
@RestController
@RequestMapping(value = "/api/v1/account", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Account", description = "Operaciones de cuenta del usuario autenticado")
public class AccountController {

    private final UserCommandService userCommandService;

    public AccountController(UserCommandService userCommandService) {
        this.userCommandService = userCommandService;
    }

    @PostMapping(value = "/change-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Cambiar la contraseña del usuario autenticado",
            description = "Requiere reenviar la contraseña actual como prueba de identidad.")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Contraseña cambiada"),
                @ApiResponse(responseCode = "400", description = "Contraseña actual inválida"),
                @ApiResponse(responseCode = "401", description = "No autenticado"),
                @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
            })
    public ResponseEntity<MessageResource> changePassword(
            @Valid @RequestBody ChangePasswordResource resource) {
        String email = currentEmail();
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResource("Sesión no válida"));
        }
        var command = new ChangePasswordCommand(email, resource.currentPassword(), resource.newPassword());
        try {
            var updated = userCommandService.handle(command);
            if (updated.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageResource("Cuenta no encontrada"));
            }
        } catch (InvalidCurrentPasswordException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResource(ex.getMessage()));
        }
        return ResponseEntity.ok(new MessageResource("Contraseña actualizada"));
    }

    /** Email del usuario autenticado, o {@code null} si no hay sesión válida. */
    private String currentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null
                || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }
        if (!(auth.getPrincipal() instanceof UserDetailsImpl ud)) {
            return null;
        }
        String email = ud.getUsername();
        return (email == null || email.isBlank()) ? null : email;
    }
}
