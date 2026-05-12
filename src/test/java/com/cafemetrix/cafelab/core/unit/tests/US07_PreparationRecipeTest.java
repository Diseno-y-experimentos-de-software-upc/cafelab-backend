package com.cafemetrix.cafelab.core.unit.tests;

import com.cafemetrix.cafelab.preparation.domain.model.aggregates.Recipe;
import com.cafemetrix.cafelab.preparation.domain.model.commands.CreateRecipeCommand;
import com.cafemetrix.cafelab.preparation.domain.model.valueobjects.ExtractionCategory;
import com.cafemetrix.cafelab.preparation.domain.model.valueobjects.ExtractionMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("US07 - Recetas de Preparación")
class US07_PreparationRecipeTest {

    @Test
    @DisplayName("crea receta detallada vinculada a una bebida del portafolio")
    void createsRecipeLinkedToPortfolio() {
        var recipe = new Recipe(new CreateRecipeCommand(
                7L, "V60 Mango", "https://cdn.example.com/v60.png", "v60",
                "coffee", "1:16", 55L, 90L, 240,
                "Bloom 45s; verter en pulsos", "Usar agua a 92C",
                "Cupping Geisha", "medium-fine"));

        assertThat(recipe.getName()).isEqualTo("V60 Mango");
        assertThat(recipe.getExtractionMethod()).isEqualTo(ExtractionMethod.V60);
        assertThat(recipe.getExtractionCategory()).isEqualTo(ExtractionCategory.COFFEE);
        assertThat(recipe.getCuppingSessionId()).isEqualTo(55L);
        assertThat(recipe.getPortfolioId()).isEqualTo(90L);
        assertThat(recipe.getSteps()).contains("Bloom");
    }
}
