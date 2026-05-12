package com.cafemetrix.cafelab.core.unit.tests;

import com.cafemetrix.cafelab.iam.application.internal.commandservices.UserCommandServiceImpl;
import com.cafemetrix.cafelab.iam.application.internal.outboundservices.hashing.HashingService;
import com.cafemetrix.cafelab.iam.application.internal.outboundservices.tokens.TokenService;
import com.cafemetrix.cafelab.iam.domain.model.aggregates.User;
import com.cafemetrix.cafelab.iam.domain.model.commands.SignInCommand;
import com.cafemetrix.cafelab.iam.domain.model.commands.SignUpCommand;
import com.cafemetrix.cafelab.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("US17 - Registro y Autenticación")
class US17_RegistrationAndAuthenticationTest {

    @Test
    @DisplayName("registra usuario con password hasheado y rol")
    void signsUpUserWithHashedPassword() {
        var repository = mock(UserRepository.class);
        var hashing = mock(HashingService.class);
        var tokens = mock(TokenService.class);
        var service = new UserCommandServiceImpl(repository, hashing, tokens);
        when(repository.existsByEmail("ana@example.com")).thenReturn(false);
        when(hashing.encode("secret")).thenReturn("hashed-secret");
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = service.handle(new SignUpCommand("ana@example.com", "secret", "barista"));

        assertThat(result).isPresent();
        assertThat(result.orElseThrow().getEmail()).isEqualTo("ana@example.com");
        assertThat(result.orElseThrow().getPassword()).isEqualTo("hashed-secret");
        assertThat(result.orElseThrow().getRole()).isEqualTo("barista");
    }

    @Test
    @DisplayName("autentica usuario valido y genera token")
    void signsInUserAndGeneratesToken() {
        var repository = mock(UserRepository.class);
        var hashing = mock(HashingService.class);
        var tokens = mock(TokenService.class);
        var service = new UserCommandServiceImpl(repository, hashing, tokens);
        var user = new User("ana@example.com", "hashed-secret", "barista");
        when(repository.findByEmail("ana@example.com")).thenReturn(Optional.of(user));
        when(hashing.matches("secret", "hashed-secret")).thenReturn(true);
        when(tokens.generateToken("ana@example.com")).thenReturn("jwt-token");

        var result = service.handle(new SignInCommand("ana@example.com", "secret"));

        assertThat(result).isPresent();
        assertThat(result.orElseThrow().left.getEmail()).isEqualTo("ana@example.com");
        assertThat(result.orElseThrow().right).isEqualTo("jwt-token");
    }
}
