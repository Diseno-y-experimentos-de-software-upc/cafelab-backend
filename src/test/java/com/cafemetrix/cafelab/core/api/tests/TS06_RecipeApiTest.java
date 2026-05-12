package com.cafemetrix.cafelab.core.api.tests;

import com.cafemetrix.cafelab.iam.infrastructure.authorization.sfs.support.CurrentProfileIdResolver;
import com.cafemetrix.cafelab.preparation.domain.model.aggregates.Recipe;
import com.cafemetrix.cafelab.preparation.domain.model.commands.CreateRecipeCommand;
import com.cafemetrix.cafelab.preparation.interfaces.acl.PreparationContextFacade;
import com.cafemetrix.cafelab.preparation.interfaces.rest.RecipesController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RecipesController.class, 
            excludeAutoConfiguration = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
@DisplayName("TS06 - API Recetas")
class TS06_RecipeApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PreparationContextFacade preparationContextFacade;

    @MockitoBean
    private CurrentProfileIdResolver currentProfileIdResolver;

    @Test
    @DisplayName("POST /api/v1/recipes crea una receta exitosamente (201)")
    void createRecipeReturnsCreated() throws Exception {
        when(currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of(7L));
        
        var recipe = new Recipe(new CreateRecipeCommand(
                7L, "Receta Test", "url", "v60", "coffee", "1:16", 1L, 1L, 300, "steps", "tips", "cupping", "fine"
        ));
        
        // Asignar ID y fecha de creación manualmente mediante reflexión para evitar NPE en el assembler
        java.lang.reflect.Field idField = com.cafemetrix.cafelab.shared.domain.model.aggregates.AuditableAbstractAggregateRoot.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(recipe, 1L);
        
        java.lang.reflect.Field createdAtField = com.cafemetrix.cafelab.shared.domain.model.aggregates.AuditableAbstractAggregateRoot.class.getDeclaredField("createdAt");
        createdAtField.setAccessible(true);
        createdAtField.set(recipe, new java.util.Date());

        when(preparationContextFacade.createRecipe(any())).thenReturn(Optional.of(recipe));
        when(preparationContextFacade.getIngredientsByRecipeId(any())).thenReturn(List.of());

        String json = """
                {
                  "name": "Receta Test",
                  "imageUrl": "url",
                  "extractionMethod": "v60",
                  "extractionCategory": "coffee",
                  "ratio": "1:16",
                  "cuppingSessionId": 1,
                  "portfolioId": 1,
                  "preparationTime": 300,
                  "steps": "steps",
                  "tips": "tips",
                  "cupping": "cupping",
                  "grindSize": "fine"
                }
                """;

        mockMvc.perform(post("/api/v1/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Receta Test"));
    }

    @Test
    @DisplayName("GET /api/v1/recipes retorna lista de recetas (200)")
    void getAllRecipesReturnsOk() throws Exception {
        when(currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of(7L));
        when(preparationContextFacade.getRecipesByUserId(7L)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/recipes"))
                .andExpect(status().isOk());
    }
}
