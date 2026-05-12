package com.cafemetrix.cafelab.core.unit.tests;

import com.cafemetrix.cafelab.profiles.domain.model.aggregates.Profile;
import com.cafemetrix.cafelab.profiles.domain.model.commands.CreateProfileCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("US15 - Secciones por Segmento")
class US15_PlanSegmentsTest {

    @Test
    @DisplayName("crea perfil con plan disponible para dashboard barista")
    void createsProfileWithBaristaPlan() {
        var profile = new Profile(new CreateProfileCommand(
                "Ana Barista", "ana@example.com", "secret", "barista",
                "Cafe Centro", "3 years", "avatar.png", "card",
                true, "barista", true));

        assertThat(profile.getPlan()).isEqualTo("barista");
        assertThat(profile.hasPlan()).isTrue();
    }
}
