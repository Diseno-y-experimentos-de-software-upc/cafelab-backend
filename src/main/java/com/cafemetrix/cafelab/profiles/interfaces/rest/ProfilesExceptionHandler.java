package com.cafemetrix.cafelab.profiles.interfaces.rest;

import com.cafemetrix.cafelab.profiles.domain.exceptions.ProfileCreationFailedException;
import com.cafemetrix.cafelab.profiles.domain.exceptions.ProfileFieldInUseException;
import com.cafemetrix.cafelab.profiles.domain.exceptions.ProfileNotFoundException;
import com.cafemetrix.cafelab.profiles.interfaces.rest.resources.ProfileFieldInUseResource;
import com.cafemetrix.cafelab.shared.interfaces.rest.resources.MessageResource;
import com.cafemetrix.cafelab.shared.interfaces.rest.support.CafeLabScopedExceptionHandlerSupport;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.cafemetrix.cafelab.profiles.interfaces.rest")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ProfilesExceptionHandler extends CafeLabScopedExceptionHandlerSupport {

    @ExceptionHandler(ProfileNotFoundException.class)
    public ResponseEntity<MessageResource> handleProfileNotFound(ProfileNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResource(ex.getMessage()));
    }

    @ExceptionHandler(ProfileCreationFailedException.class)
    public ResponseEntity<MessageResource> handleProfileCreationFailed(ProfileCreationFailedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResource(ex.getMessage()));
    }

    @ExceptionHandler(ProfileFieldInUseException.class)
    public ResponseEntity<ProfileFieldInUseResource> handleProfileFieldInUse(
            ProfileFieldInUseException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ProfileFieldInUseResource(ex.field().wireName(), ex.getMessage()));
    }
}
