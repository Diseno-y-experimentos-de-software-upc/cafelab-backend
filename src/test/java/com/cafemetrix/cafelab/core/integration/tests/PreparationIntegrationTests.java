package com.cafemetrix.cafelab.core.integration.tests;

// US07: Creación de Recetas de Preparación
// US09: Portafolio de Bebidas

import com.cafemetrix.cafelab.iam.domain.model.aggregates.User;
import com.cafemetrix.cafelab.iam.infrastructure.authorization.sfs.support.CurrentProfileIdResolver;
import com.cafemetrix.cafelab.preparation.application.acl.PreparationContextFacadeImpl;
import com.cafemetrix.cafelab.preparation.domain.model.aggregates.Portfolio;
import com.cafemetrix.cafelab.preparation.domain.model.aggregates.Recipe;
import com.cafemetrix.cafelab.preparation.domain.model.valueobjects.ExtractionCategory;
import com.cafemetrix.cafelab.preparation.domain.model.valueobjects.ExtractionMethod;
import com.cafemetrix.cafelab.preparation.interfaces.acl.PreparationContextFacade;
import com.cafemetrix.cafelab.preparation.interfaces.rest.PortfoliosController;
import com.cafemetrix.cafelab.preparation.interfaces.rest.RecipesController;
import com.cafemetrix.cafelab.preparation.interfaces.rest.dto.CreatePortfolioResource;
import com.cafemetrix.cafelab.preparation.interfaces.rest.dto.CreateRecipeResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class PreparationIntegrationTests {
    // Portafolio de Bebidas - Portfolios
    private PortfoliosController portfoliosController;

    // Recetas de Preparación - Recipes
    private RecipesController recipesController;

    // Facades
    private PreparationContextFacade preparationContextFacade;
    private CurrentProfileIdResolver currentProfileIdResolver;

    @BeforeEach
    void setUp() {
        // Facades
        preparationContextFacade = Mockito.mock(PreparationContextFacadeImpl.class);
        currentProfileIdResolver = Mockito.mock(CurrentProfileIdResolver.class);

        // Portafolio de Bebidas
        portfoliosController = new PortfoliosController(preparationContextFacade, currentProfileIdResolver);

        // Recetas de Preparación
        recipesController = new RecipesController(preparationContextFacade, currentProfileIdResolver);
    }

    // US09: Portafolio de Bebidas
    @Test
    void createPorfolio_ReturnsCreated_WhenValidInput() {
        var resource = new CreatePortfolioResource(
                "Portafolio de Prueba"
        );

        Portfolio mockPortfolio = Mockito.mock(Portfolio.class);

        Mockito.when(mockPortfolio.getName()).thenReturn("Portafolio de Prueba");
        Mockito.when(mockPortfolio.getCreatedAt()).thenReturn(new Date());
        Mockito.when(mockPortfolio.getUpdatedAt()).thenReturn(new Date());

        Mockito.when(currentProfileIdResolver.resolveProfileId())
                .thenReturn(Optional.of(1L));

        User mockUser = Mockito.mock(User.class);

        Mockito.when(mockUser.getId()).thenReturn(1L);

        Mockito.when(preparationContextFacade.createPortfolio(1L, "Portafolio de Prueba"))
                .thenReturn(1L);

        Mockito.when(preparationContextFacade.createPortfolio(
                Mockito.anyLong(),
                Mockito.anyString()
        )).thenReturn(1L);

        Mockito.when(preparationContextFacade.getPortfolioByIdForUser(1L, 1L))
                .thenReturn(Optional.of(mockPortfolio));

        Mockito.when(preparationContextFacade.getPortfoliosByUserId(1L))
                .thenReturn(java.util.List.of(mockPortfolio));

        var response = portfoliosController.createPortfolio(resource);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }

    // US07: Creación de Recetas de Preparación
    @Test
    void createRecipe_ReturnsCreated_WhenValidInput() {
        var resource = new CreateRecipeResource(
                "Receta de Prueba",
                "image.com",
                "espresso",
                "Pour Over",
                "1:15",
                1L,
                1L,
                14,
                "1. Calentar agua. 2. Enjuagar filtro. 3. Agregar café molido. 4. Verter agua lentamente en círculos. 5. Dejar reposar y servir.",
                "Usar molienda media para obtener mejores resutados",
                "Notas de aroma floral.",
                "Media"
        );

        Recipe mockRecipe = Mockito.mock(Recipe.class);

        Mockito.when(mockRecipe.getName()).thenReturn("Receta de Prueba");
        Mockito.when(mockRecipe.getImageUrl()).thenReturn("image.com");
        Mockito.when(mockRecipe.getExtractionMethod()).thenReturn(ExtractionMethod.ESPRESSO);
        Mockito.when(mockRecipe.getExtractionCategory()).thenReturn(ExtractionCategory.ESPRESSO);
        Mockito.when(mockRecipe.getRatio()).thenReturn("1:15");
        Mockito.when(mockRecipe.getCuppingSessionId()).thenReturn(1L);
        Mockito.when(mockRecipe.getPortfolioId()).thenReturn(1L);
        Mockito.when(mockRecipe.getPreparationTime()).thenReturn(14);
        Mockito.when(mockRecipe.getSteps()).thenReturn("1. Calentar agua. 2. Enjuagar filtro. 3. Agregar café molido. 4. Verter agua lentamente en círculos. 5. Dejar reposar y servir.");
        Mockito.when(mockRecipe.getTips()).thenReturn("Usar molienda media para obtener mejores resutados");
        Mockito.when(mockRecipe.getCupping()).thenReturn("Notas de aroma floral.");
        Mockito.when(mockRecipe.getGrindSize()).thenReturn("Media");

        Mockito.when(mockRecipe.getCreatedAt()).thenReturn(new Date());
        Mockito.when(mockRecipe.getUpdatedAt()).thenReturn(new Date());

        Mockito.when(currentProfileIdResolver.resolveProfileId())
                .thenReturn(Optional.of(1L));

        Mockito.when(preparationContextFacade.createRecipe(
                Mockito.any()
        )).thenReturn(Optional.of(mockRecipe));

        Mockito.when(preparationContextFacade.getIngredientsByRecipeId(1L))
                .thenReturn(List.of());

        var response = recipesController.createRecipe(resource);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }
}
