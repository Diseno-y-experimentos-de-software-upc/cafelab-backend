package com.cafemetrix.cafelab.core.unit.tests;

import com.cafemetrix.cafelab.profiles.domain.model.aggregates.Profile;
import com.cafemetrix.cafelab.profiles.domain.model.commands.CreateProfileCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("US19 - Selección de Plan")
class US19_PlanSelectionTest {

    @Test
    @DisplayName("permite seleccionar plan full")
    void selectsFullPlan() {
        var profile = new Profile(new CreateProfileCommand(
                "Cafe Owner", "owner@example.com", "secret", "owner",
                "Cafe Norte", "5 years", "avatar.png", "card",
                false, "owner", true));

        profile.updatePlan("full");
        profile.updateHasPlanStatus(true);

        assertThat(profile.getPlan()).isEqualTo("full");
        assertThat(profile.hasPlan()).isTrue();
    }
}
