package com.cafemetrix.cafelab.core.integration.tests;

import com.cafemetrix.cafelab.iam.domain.exceptions.SignInFailedException;
import com.cafemetrix.cafelab.iam.domain.model.aggregates.User;
import com.cafemetrix.cafelab.iam.domain.model.commands.SignInCommand;
import com.cafemetrix.cafelab.iam.domain.model.commands.SignUpCommand;
import com.cafemetrix.cafelab.iam.domain.services.UserCommandService;
import com.cafemetrix.cafelab.iam.interfaces.rest.AuthenticationController;
import com.cafemetrix.cafelab.iam.interfaces.rest.resources.SignInResource;
import com.cafemetrix.cafelab.iam.interfaces.rest.resources.SignUpResource;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import java.util.Optional;

public class UserIntegrationTests {
    private UserCommandService userCommandService;
    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        userCommandService = Mockito.mock(UserCommandService.class);
        authenticationController = new AuthenticationController(userCommandService);
    }

    // US17: Registro y Autenticación de Usuario

    @Test
    void signUp_ReturnsCreated_WhenValidInput() {
        User mockUser = Mockito.mock(User.class);

        Mockito.when(mockUser.getId()).thenReturn(1L);
        Mockito.when(mockUser.getEmail()).thenReturn("test@test.com");
        Mockito.when(mockUser.getPassword()).thenReturn("password123");
        Mockito.when(mockUser.getRole()).thenReturn("ROLE_USER");

        var mockToken = "mock-jwt-token";

        SignUpResource resource = new SignUpResource(
                "test@test.com",
                "password123",
                "ROLE_USER"
        );

        Mockito.when(userCommandService.handle(Mockito.any(SignUpCommand.class)))
                .thenReturn(Optional.of(mockUser));

        Mockito.when(userCommandService.handle(Mockito.any(SignInCommand.class)))
                .thenReturn(Optional.of(ImmutablePair.of(mockUser, mockToken)));

        var response = authenticationController.signUp(resource);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(mockUser.getEmail(), response.getBody().email());

    }

    @Test
    void signIn_ReturnsOK_WhenValidInput() {
        User mockUser = Mockito.mock(User.class);

        Mockito.when(mockUser.getId()).thenReturn(1L);
        Mockito.when(mockUser.getEmail()).thenReturn("test@test.com");
        Mockito.when(mockUser.getPassword()).thenReturn("password123");
        Mockito.when(mockUser.getRole()).thenReturn("ROLE_USER");

        var mockToken = "mock-jwt-token";

        SignInResource resource = new SignInResource(
                "test@test.com",
                "password123"
        );

        Mockito.when(userCommandService.handle(Mockito.any(SignInCommand.class)))
                .thenReturn(Optional.of(ImmutablePair.of(mockUser, mockToken)));

        var response = authenticationController.signIn(resource);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());

    }

    @Test
    void signIn_ReturnsNotFound_WhenInvalidInput() {
        SignInResource resource = new SignInResource(
                "tst@test.com",
                "wrong-pass/"
        );

        Mockito.when(userCommandService.handle(Mockito.any(SignInCommand.class)))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(
                SignInFailedException.class,
                () -> authenticationController.signIn(resource)
        );
    }
}
