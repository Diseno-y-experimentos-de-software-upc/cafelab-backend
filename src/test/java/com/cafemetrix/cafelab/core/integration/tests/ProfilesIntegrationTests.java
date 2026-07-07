package com.cafemetrix.cafelab.core.integration.tests;

import com.cafemetrix.cafelab.profiles.domain.model.aggregates.Profile;
import com.cafemetrix.cafelab.profiles.domain.model.commands.CreateProfileCommand;
import com.cafemetrix.cafelab.profiles.domain.model.queries.GetProfileByEmailQuery;
import com.cafemetrix.cafelab.profiles.domain.services.ProfileCommandService;
import com.cafemetrix.cafelab.profiles.domain.services.ProfileQueryService;
import com.cafemetrix.cafelab.profiles.interfaces.rest.ProfilesController;
import com.cafemetrix.cafelab.profiles.interfaces.rest.resources.CreateProfileResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import java.util.Optional;

 // US18: Perfil Personalizado

public class ProfilesIntegrationTests {
    private ProfileCommandService profileCommandService;
    private ProfileQueryService profileQueryService;
    private ProfilesController profilesController;

    @BeforeEach
    void setUp() {
        profileCommandService = Mockito.mock(ProfileCommandService.class);
        profileQueryService = Mockito.mock(ProfileQueryService.class);
        profilesController = new ProfilesController(profileCommandService, profileQueryService);
    }

    @Test
    void createProfile_ReturnsCreated_WhenValidInput() {
        var resource = new CreateProfileResource(
                "test test1",
                "test@test.com",
                "password123",
                "barista",
                "CafeLab",
                "2 years",
                "profile.png",
                "card",
                true,
                "basic",
                true
        );

        var mockProfile = new Profile(
                "test test1",
                "test@test.com",
                "password123",
                "barista",
                "CafeLab",
                "2 years",
                "profile.png",
                "card",
                true,
                "basic",
                true
        );

        Mockito.when(profileCommandService.handle(Mockito.any(CreateProfileCommand.class)))
                .thenReturn(Optional.of(mockProfile));

        var response = profilesController.createProfile(resource);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("test@test.com", response.getBody().email());
    }

    @Test
    void getProfileById_ReturnsProfile_WhenProfileExists() {
        var mockProfile = new Profile(
                "test test1",
                "test@test.com",
                "password123",
                "barista",
                "CafeLab",
                "2 years",
                "profile.png",
                "card",
                true,
                "basic",
                true
        );

        Mockito.when(profileQueryService.handle(Mockito.any(GetProfileByEmailQuery.class)))
                .thenReturn(Optional.of(mockProfile));

        var response = profilesController.getProfileByEmail("test@test.com");

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());

    }
}
