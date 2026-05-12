package com.cafemetrix.cafelab.core.unit.tests;

import com.cafemetrix.cafelab.profiles.domain.model.aggregates.Profile;
import com.cafemetrix.cafelab.profiles.domain.model.commands.CreateProfileCommand;
import com.cafemetrix.cafelab.profiles.interfaces.rest.resources.CreateProfileResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("US18 - Perfil Personalizado")
class US18_PersonalizedProfileTest {

    @Test
    @DisplayName("permite actualizar el perfil")
    void updatesProfile() {
        var profile = new Profile(new CreateProfileCommand(
                "Cafe Owner", "owner@example.com", "secret", "owner",
                "Cafe Norte", "5 years", "avatar.png", "card",
                false, "owner", true));

        profile.updateName("Cafe Owner Pro");
        profile.updateProfilePicture("new-avatar.png");

        assertThat(profile.getName()).isEqualTo("Cafe Owner Pro");
        assertThat(profile.getProfilePicture()).isEqualTo("new-avatar.png");
    }

    @Test
    @DisplayName("solo acepta roles que corresponden a dashboards disponibles")
    void rejectsUnsupportedRole() {
        assertThatThrownBy(() -> new CreateProfileResource(
                "Admin", "admin@example.com", "secret", "admin",
                "Cafe Admin", "1 year", "avatar.png", "card",
                true, "full", true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("barista");
    }
}
