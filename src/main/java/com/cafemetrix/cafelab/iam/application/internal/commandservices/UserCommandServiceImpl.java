package com.cafemetrix.cafelab.iam.application.internal.commandservices;

import com.cafemetrix.cafelab.iam.application.internal.outboundservices.hashing.HashingService;
import com.cafemetrix.cafelab.iam.application.internal.outboundservices.tokens.TokenService;
import com.cafemetrix.cafelab.iam.domain.model.aggregates.User;
import com.cafemetrix.cafelab.iam.domain.model.commands.SignInCommand;
import com.cafemetrix.cafelab.iam.domain.model.commands.SignUpCommand;
import com.cafemetrix.cafelab.iam.domain.services.UserCommandService;
import com.cafemetrix.cafelab.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Misma lógica de sign-in / sign-up que {@code meditrack-platform} (sin organization/admin).
 */
@Service
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;

    public UserCommandServiceImpl(UserRepository userRepository, HashingService hashingService, TokenService tokenService) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
    }

    @Override
    public Optional<ImmutablePair<User, String>> handle(SignInCommand command) {
        var user = userRepository.findByEmail(command.email());
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        if (!hashingService.matches(command.password(), user.get().getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }
        var token = tokenService.generateToken(user.get().getEmail());
        return Optional.of(ImmutablePair.of(user.get(), token));
    }

    @Override
    @Transactional
    public Optional<User> handle(SignUpCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new IllegalArgumentException("Email already exists");
        }
        var hashedPassword = hashingService.encode(command.password());
        var user = new User(command.email(), hashedPassword, command.role());
        var savedUser = userRepository.save(user);
        return Optional.of(savedUser);
    }
}
