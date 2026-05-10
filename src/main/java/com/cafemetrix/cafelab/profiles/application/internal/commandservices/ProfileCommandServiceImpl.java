package com.cafemetrix.cafelab.profiles.application.internal.commandservices;

import com.cafemetrix.cafelab.profiles.domain.exceptions.ProfileFieldInUseException;
import com.cafemetrix.cafelab.profiles.domain.model.aggregates.Profile;
import com.cafemetrix.cafelab.profiles.domain.model.commands.CreateProfileCommand;
import com.cafemetrix.cafelab.profiles.domain.model.events.ProfileCreatedEvent;
import com.cafemetrix.cafelab.profiles.domain.model.commands.UpdateProfileCommand;
import com.cafemetrix.cafelab.profiles.domain.model.valueobjects.EmailAddress;
import com.cafemetrix.cafelab.profiles.domain.services.ProfileCommandService;
import com.cafemetrix.cafelab.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service
public class ProfileCommandServiceImpl implements ProfileCommandService {
    private final ProfileRepository profileRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Constructor
     *
     * @param profileRepository The {@link ProfileRepository} instance
     */
    public ProfileCommandServiceImpl(ProfileRepository profileRepository, ApplicationEventPublisher eventPublisher) {
        this.profileRepository = profileRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<Profile> handle(CreateProfileCommand command) {
        var emailAddress = new EmailAddress(command.email());
        if (profileRepository.existsByEmailAddress(emailAddress)) {
            throw new IllegalArgumentException("Profile with email address already exists");
        }
        var profile = new Profile(command.name(), command.email(), command.password(), command.role(),
                command.cafeteriaName(), command.experience(), command.profilePicture(),
                command.paymentMethod(), command.isFirstLogin(), command.plan(), command.hasPlan());
        profileRepository.save(profile);
        eventPublisher.publishEvent(new ProfileCreatedEvent(command.email(), command.password(), command.role()));
        return Optional.of(profile);
    }

    @Override
    public Optional<Profile> handle(UpdateProfileCommand command) {
        var profile = profileRepository.findById(command.userId());
        if (profile.isEmpty()) {
            return Optional.empty();
        }

        var updatedProfile = profile.get();
        // Para campos únicos validamos colisión sólo cuando cambian respecto al valor actual.
        if (command.email() != null && !command.email().isBlank()) {
            String newEmail = command.email().trim();
            String currentEmail = updatedProfile.getEmailAddress();
            if (!equalsNormalized(newEmail, currentEmail)) {
                if (profileRepository.existsByNormalizedEmailExcludingId(
                        newEmail.toLowerCase(Locale.ROOT), updatedProfile.getId())) {
                    throw new ProfileFieldInUseException(
                            ProfileFieldInUseException.Field.EMAIL,
                            "El correo ya está en uso por otra cuenta");
                }
            }
            updatedProfile.updateEmailAddress(newEmail);
        }
        if (command.name() != null && !command.name().isBlank()) {
            String newName = command.name().trim();
            if (!equalsNormalized(newName, updatedProfile.getName())) {
                if (profileRepository.existsByNormalizedNameExcludingId(
                        newName.toLowerCase(Locale.ROOT), updatedProfile.getId())) {
                    throw new ProfileFieldInUseException(
                            ProfileFieldInUseException.Field.NAME,
                            "El nombre ya está en uso por otra cuenta");
                }
            }
            updatedProfile.updateName(newName);
        }
        if (command.cafeteriaName() != null) {
            String newCafeteria = command.cafeteriaName().trim();
            if (newCafeteria.isEmpty()) {
                updatedProfile.updateCafeteriaName(newCafeteria);
            } else if (!equalsNormalized(newCafeteria, updatedProfile.getCafeteriaName())) {
                if (profileRepository.existsByNormalizedCafeteriaNameExcludingId(
                        newCafeteria.toLowerCase(Locale.ROOT), updatedProfile.getId())) {
                    throw new ProfileFieldInUseException(
                            ProfileFieldInUseException.Field.CAFETERIA_NAME,
                            "El nombre de la cafetería ya está en uso por otra cuenta");
                }
                updatedProfile.updateCafeteriaName(newCafeteria);
            } else {
                updatedProfile.updateCafeteriaName(newCafeteria);
            }
        }
        if (command.experience() != null) updatedProfile.updateExperience(command.experience());
        if (command.paymentMethod() != null) updatedProfile.updatePaymentMethod(command.paymentMethod());
        if (command.isFirstLogin() != null) updatedProfile.updateFirstLoginStatus(command.isFirstLogin());
        if (command.plan() != null) updatedProfile.updatePlan(command.plan());
        if (command.hasPlan() != null) updatedProfile.updateHasPlanStatus(command.hasPlan());

        profileRepository.save(updatedProfile);
        return Optional.of(updatedProfile);
    }

    private static boolean equalsNormalized(String a, String b) {
        if (a == null || b == null) {
            return a == null && b == null;
        }
        return a.trim().equalsIgnoreCase(b.trim());
    }
}
