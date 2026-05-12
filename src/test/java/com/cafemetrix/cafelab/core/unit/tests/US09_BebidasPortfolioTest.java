package com.cafemetrix.cafelab.core.unit.tests;

import com.cafemetrix.cafelab.preparation.domain.model.aggregates.Portfolio;
import com.cafemetrix.cafelab.preparation.domain.model.commands.CreatePortfolioCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("US09 - Portafolio de Bebidas")
class US09_BebidasPortfolioTest {

    @Test
    @DisplayName("crea una entrada en el portafolio de bebidas")
    void createsPortfolioEntry() {
        var portfolio = new Portfolio(new CreatePortfolioCommand(5L, "Espresso"));

        assertThat(portfolio.getUserId()).isEqualTo(5L);
        assertThat(portfolio.getName()).isEqualTo("Espresso");
    }
}
