package com.cafemetrix.cafelab.core.unit.tests;

import com.cafemetrix.cafelab.production.domain.model.aggregates.RoastProfile;
import com.cafemetrix.cafelab.production.domain.model.commands.CreateRoastProfileCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("US03 - Perfil de Tueste")
class US03_RoastProfileTest {

    @Test
    @DisplayName("crea un perfil personalizado y permite marcarlo como favorito")
    void createsAndFavoritesRoastProfile() {
        var profile = new RoastProfile(new CreateRoastProfileCommand(
                7L, "Filtro claro", "Ligero", 12, 160.0, 208.0, 33L, false));

        profile.toggleFavorite();

        assertThat(profile.getUserId()).isEqualTo(7L);
        assertThat(profile.getName()).isEqualTo("Filtro claro");
        assertThat(profile.getCoffeeLotId()).isEqualTo(33L);
        assertThat(profile.getIsFavorite()).isTrue();
    }

    @Test
    @DisplayName("rechaza temperaturas fuera del rango operativo")
    void rejectsOutOfRangeTemperature() {
        assertThatThrownBy(() -> new RoastProfile(new CreateRoastProfileCommand(
                7L, "Perfil invalido", "Ligero", 12, 160.0, 350.0, 33L, false)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("temperatura");
    }
}
