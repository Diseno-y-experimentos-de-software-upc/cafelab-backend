package com.cafemetrix.cafelab.iam.domain.services;

import com.cafemetrix.cafelab.iam.domain.model.aggregates.User;
import com.cafemetrix.cafelab.iam.domain.model.commands.ChangePasswordCommand;
import com.cafemetrix.cafelab.iam.domain.model.commands.SignInCommand;
import com.cafemetrix.cafelab.iam.domain.model.commands.SignUpCommand;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Optional;

public interface UserCommandService {
    Optional<ImmutablePair<User, String>> handle(SignInCommand command);
    Optional<User> handle(SignUpCommand command);

    /**
     * Cambia la contraseña del usuario tras verificar la actual.
     * @return el usuario actualizado, vacío si no existía.
     * @throws com.cafemetrix.cafelab.iam.domain.exceptions.InvalidCurrentPasswordException
     *         si la contraseña actual no coincide.
     */
    Optional<User> handle(ChangePasswordCommand command);
}
