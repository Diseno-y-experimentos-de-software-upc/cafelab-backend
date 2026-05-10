package com.cafemetrix.cafelab.iam.infrastructure.persistence;

import com.cafemetrix.cafelab.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.cafemetrix.cafelab.profiles.domain.model.aggregates.Profile;
import com.cafemetrix.cafelab.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Enlaza {@code profiles.user_id} cuando el email del perfil coincide con {@code users.email} del bounded
 * context IAM.
 */
@Component
@Order
public class ProfileIamUserLinkBackfill implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(ProfileIamUserLinkBackfill.class);

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public ProfileIamUserLinkBackfill(ProfileRepository profileRepository, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            for (Profile p : profileRepository.findByIamUserIdIsNull()) {
                String email = p.getEmailAddress();
                if (email == null || email.isBlank()) {
                    continue;
                }
                String trimmed = email.trim();
                userRepository
                        .findByEmail(trimmed)
                        .or(() -> userRepository.findByEmailIgnoreCase(trimmed))
                        .ifPresent(
                                u -> {
                                    p.setIamUserId(u.getId());
                                    profileRepository.save(p);
                                    log.debug("Enlazado perfil id={} con users.id={}", p.getId(), u.getId());
                                });
            }
        } catch (Exception e) {
            log.warn("No se pudo enlazar profiles.user_id con users (se reintentará al reiniciar): {}", e.getMessage());
        }
    }
}
