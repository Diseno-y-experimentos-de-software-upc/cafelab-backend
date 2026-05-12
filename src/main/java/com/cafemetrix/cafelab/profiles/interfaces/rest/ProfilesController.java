package com.cafemetrix.cafelab.profiles.interfaces.rest;

import com.cafemetrix.cafelab.profiles.domain.exceptions.ProfileCreationFailedException;
import com.cafemetrix.cafelab.profiles.domain.exceptions.ProfileNotFoundException;
import com.cafemetrix.cafelab.profiles.domain.model.queries.CheckProfileFieldAvailabilityQuery;
import com.cafemetrix.cafelab.profiles.domain.model.queries.GetAllProfilesQuery;
import com.cafemetrix.cafelab.profiles.domain.model.queries.GetProfileByEmailQuery;
import com.cafemetrix.cafelab.profiles.domain.model.queries.GetProfileByIdQuery;
import com.cafemetrix.cafelab.profiles.domain.services.ProfileCommandService;
import com.cafemetrix.cafelab.profiles.domain.services.ProfileQueryService;
import com.cafemetrix.cafelab.profiles.interfaces.rest.resources.CreateProfileResource;
import com.cafemetrix.cafelab.profiles.interfaces.rest.resources.ProfileFieldsAvailabilityResource;
import com.cafemetrix.cafelab.profiles.interfaces.rest.resources.ProfileResource;
import com.cafemetrix.cafelab.profiles.interfaces.rest.resources.UpdateProfileResource;
import com.cafemetrix.cafelab.profiles.interfaces.rest.transform.CreateProfileCommandFromResourceAssembler;
import com.cafemetrix.cafelab.profiles.interfaces.rest.transform.ProfileResourceFromEntityAssembler;
import com.cafemetrix.cafelab.profiles.domain.model.valueobjects.EmailAddress;

import com.cafemetrix.cafelab.profiles.interfaces.rest.transform.UpdateProfileCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/profiles", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Profiles", description = "Available Profile Endpoints")
public class ProfilesController {
    private final ProfileCommandService profileCommandService;
    private final ProfileQueryService profileQueryService;

    /**
     * Constructor
     * @param profileCommandService The {@link ProfileCommandService} instance
     * @param profileQueryService The {@link ProfileQueryService} instance
     */
    public ProfilesController(ProfileCommandService profileCommandService, ProfileQueryService profileQueryService) {
        this.profileCommandService = profileCommandService;
        this.profileQueryService = profileQueryService;
    }

    /**
     * Create a new profile
     * @param resource The {@link CreateProfileResource} instance
     * @return A {@link ProfileResource} resource for the created profile, or a bad request response if the profile could not be created.
     */
    @PostMapping
    @Operation(summary = "Create a new profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Profile created"),
            @ApiResponse(responseCode = "400", description = "Bad request")})
    public ResponseEntity<ProfileResource> createProfile(@RequestBody CreateProfileResource resource) {
        var createProfileCommand = CreateProfileCommandFromResourceAssembler.toCommandFromResource(resource);
        var profile = profileCommandService.handle(createProfileCommand);
        if (profile.isEmpty()) throw new ProfileCreationFailedException();
        var createdProfile = profile.get();
        var profileResource = ProfileResourceFromEntityAssembler.toResourceFromEntity(createdProfile);
        return new ResponseEntity<>(profileResource, HttpStatus.CREATED);
    }

    /**
     * Get a profile by ID
     * @param userId The profile (user) ID
     * @return A {@link ProfileResource} resource for the profile, or a not found response if the profile could not be found.
     */
    @GetMapping("/{userId}")
    @Operation(summary = "Get a profile by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile found"),
            @ApiResponse(responseCode = "404", description = "Profile not found")})
    public ResponseEntity<ProfileResource> getProfileById(@PathVariable Long userId) {
        var getProfileByIdQuery = new GetProfileByIdQuery(userId);
        var profile = profileQueryService.handle(getProfileByIdQuery);
        if (profile.isEmpty()) throw new ProfileNotFoundException(userId);
        var profileEntity = profile.get();
        var profileResource = ProfileResourceFromEntityAssembler.toResourceFromEntity(profileEntity);
        return ResponseEntity.ok(profileResource);
    }

    @GetMapping(params = "email")
    @Operation(summary = "Get profile by email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile found"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public ResponseEntity<ProfileResource> getProfileByEmail(@RequestParam String email) {
        var profile = profileQueryService.handle(new GetProfileByEmailQuery(new EmailAddress(email)));
        if (profile.isEmpty()) throw new ProfileNotFoundException(email);
        var profileResource = ProfileResourceFromEntityAssembler.toResourceFromEntity(profile.get());
        return ResponseEntity.ok(profileResource);
    }

    /**
     * Verifica si los campos únicos (email, name, cafeteriaName) propuestos para un perfil ya están
     * en uso por otro perfil distinto al identificado por {@code excludingUserId}. Cualquier
     * parámetro en blanco se ignora.
     */
    @GetMapping("/availability")
    @Operation(
            summary = "Check uniqueness of email/name/cafeteriaName for a profile update",
            description = "Returns booleans indicating which proposed values are already taken by another profile.")
    @ApiResponses(
            value = {@ApiResponse(responseCode = "200", description = "Availability resolved")})
    public ResponseEntity<ProfileFieldsAvailabilityResource> checkAvailability(
            @RequestParam(value = "excludingUserId", required = false) Long excludingUserId,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "cafeteriaName", required = false) String cafeteriaName) {
        var availability =
                profileQueryService.handle(
                        new CheckProfileFieldAvailabilityQuery(
                                excludingUserId, email, name, cafeteriaName));
        return ResponseEntity.ok(
                new ProfileFieldsAvailabilityResource(
                        availability.emailTaken(),
                        availability.nameTaken(),
                        availability.cafeteriaNameTaken()));
    }

    /**
     * Get all profiles
     * @return A list of {@link ProfileResource} resources for all profiles, or a not found response if no profiles are found.
     */
    @GetMapping
    @Operation(summary = "Get all profiles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profiles found"),
            @ApiResponse(responseCode = "404", description = "Profiles not found")})
    public ResponseEntity<List<ProfileResource>> getAllProfiles() {
        var profiles = profileQueryService.handle(new GetAllProfilesQuery());
        if (profiles.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        var profileResources = profiles.stream()
                .map(ProfileResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(profileResources);
    }

    @PatchMapping("/{userId}")
    @Operation(summary = "Update profile", description = "Update an existing profile.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "404", description = "Profile not found.")})
    public ResponseEntity<ProfileResource> updateProfile(@PathVariable Long userId, @RequestBody UpdateProfileResource resource) {
        var updateProfileCommand = UpdateProfileCommandFromResourceAssembler.toCommandFromResource(userId, resource);
        var updatedProfile = profileCommandService.handle(updateProfileCommand);

        if (updatedProfile.isEmpty()) {
            throw new ProfileNotFoundException(userId);
        }

        var profileResource = ProfileResourceFromEntityAssembler.toResourceFromEntity(updatedProfile.get());
        return ResponseEntity.ok(profileResource);
    }

}
