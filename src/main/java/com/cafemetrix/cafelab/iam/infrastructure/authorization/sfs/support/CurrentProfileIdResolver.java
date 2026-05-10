package com.cafemetrix.cafelab.iam.infrastructure.authorization.sfs.support;

import com.cafemetrix.cafelab.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.cafemetrix.cafelab.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.cafemetrix.cafelab.profiles.domain.model.aggregates.Profile;
import com.cafemetrix.cafelab.profiles.domain.model.queries.GetProfileByEmailQuery;
import com.cafemetrix.cafelab.profiles.domain.model.queries.GetProfileByIamUserIdQuery;
import com.cafemetrix.cafelab.profiles.domain.model.valueobjects.EmailAddress;
import com.cafemetrix.cafelab.profiles.domain.services.ProfileQueryService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Resuelve {@code profiles.id} con el email del JWT (subject del token) y, si existe, {@code profiles.user_id}.
 */
@Component
public class CurrentProfileIdResolver {

    private final ProfileQueryService profileQueryService;
    private final UserRepository userRepository;

    public CurrentProfileIdResolver(ProfileQueryService profileQueryService, UserRepository userRepository) {
        this.profileQueryService = profileQueryService;
        this.userRepository = userRepository;
    }

    public Optional<Long> resolveProfileId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        if (!(auth.getPrincipal() instanceof UserDetailsImpl ud)) {
            return Optional.empty();
        }
        String email = ud.getUsername();
        return userRepository
                .findByEmail(email)
                .or(() -> userRepository.findByEmailIgnoreCase(email))
                .flatMap(
                        user -> {
                            Optional<Profile> linked =
                                    profileQueryService.handle(new GetProfileByIamUserIdQuery(user.getId()));
                            if (linked.isPresent()) {
                                return linked.map(Profile::getId);
                            }
                            return profileQueryService
                                    .handle(new GetProfileByEmailQuery(new EmailAddress(email)))
                                    .map(Profile::getId);
                        });
    }
}
