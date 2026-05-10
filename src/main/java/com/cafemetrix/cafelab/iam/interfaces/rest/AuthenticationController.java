package com.cafemetrix.cafelab.iam.interfaces.rest;

import com.cafemetrix.cafelab.iam.domain.exceptions.SignInFailedException;
import com.cafemetrix.cafelab.iam.domain.exceptions.SignUpFailedException;
import com.cafemetrix.cafelab.iam.domain.model.commands.SignInCommand;
import com.cafemetrix.cafelab.iam.domain.services.UserCommandService;
import com.cafemetrix.cafelab.iam.interfaces.rest.resources.AuthenticatedUserResource;
import com.cafemetrix.cafelab.iam.interfaces.rest.resources.SignInResource;
import com.cafemetrix.cafelab.iam.interfaces.rest.resources.SignUpResource;
import com.cafemetrix.cafelab.iam.interfaces.rest.transform.AuthenticatedUserResourceFromEntityAssembler;
import com.cafemetrix.cafelab.iam.interfaces.rest.transform.SignInCommandFromResourceAssembler;
import com.cafemetrix.cafelab.iam.interfaces.rest.transform.SignUpCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/authentication", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Authentication", description = "IAM basado en email y JWT")
public class AuthenticationController {
    private final UserCommandService userCommandService;

    public AuthenticationController(UserCommandService userCommandService) {
        this.userCommandService = userCommandService;
    }

    @PostMapping("/sign-in")
    @Operation(summary = "Sign-in", description = "Email y contraseña.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación correcta."),
            @ApiResponse(responseCode = "404", description = "Credenciales inválidas.")})
    public ResponseEntity<AuthenticatedUserResource> signIn(@Valid @RequestBody SignInResource signInResource) {
        var signInCommand = SignInCommandFromResourceAssembler.toCommandFromResource(signInResource);
        var authenticatedUser = userCommandService.handle(signInCommand);
        if (authenticatedUser.isEmpty()) {
            throw new SignInFailedException();
        }
        var authenticatedUserResource =
                AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(
                        authenticatedUser.get().getLeft(), authenticatedUser.get().getRight());
        return ResponseEntity.ok(authenticatedUserResource);
    }

    @PostMapping("/sign-up")
    @Operation(summary = "Sign-up", description = "Registro; devuelve token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado y sesión iniciada."),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida.")})
    public ResponseEntity<AuthenticatedUserResource> signUp(@Valid @RequestBody SignUpResource signUpResource) {
        var signUpCommand = SignUpCommandFromResourceAssembler.toCommandFromResource(signUpResource);
        var user = userCommandService.handle(signUpCommand);
        if (user.isEmpty()) {
            throw new SignUpFailedException();
        }
        var signInCommand = new SignInCommand(signUpResource.email(), signUpResource.password());
        var authenticatedUser = userCommandService.handle(signInCommand);
        if (authenticatedUser.isEmpty()) {
            throw new SignUpFailedException();
        }
        var resource =
                AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(
                        authenticatedUser.get().getLeft(), authenticatedUser.get().getRight());
        return new ResponseEntity<>(resource, HttpStatus.CREATED);
    }
}
