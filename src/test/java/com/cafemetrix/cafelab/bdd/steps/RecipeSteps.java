package com.cafemetrix.cafelab.bdd.steps;

import com.cafemetrix.cafelab.preparation.domain.model.aggregates.Recipe;
import com.cafemetrix.cafelab.preparation.domain.model.valueobjects.ExtractionMethod;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class RecipeSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SharedSteps shared;

    @Given("un barista autenticado con perfil id {int} y receta lista para crear")
    public void unBaristaAutenticadoConPerfilIdYRecetaListaParaCrear(int profileId) {
        when(shared.currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of((long) profileId));
        var mockRecipe = mock(Recipe.class);
        when(mockRecipe.getId()).thenReturn(50L);
        when(mockRecipe.getUserId()).thenReturn((long) profileId);
        when(mockRecipe.getName()).thenReturn("Espresso Etiopia");
        when(mockRecipe.getImageUrl()).thenReturn("http://example.com/img");
        when(mockRecipe.getExtractionMethod()).thenReturn(ExtractionMethod.ESPRESSO);
        when(mockRecipe.getExtractionCategory()).thenReturn(null);
        when(mockRecipe.getRatio()).thenReturn("1:2");
        when(mockRecipe.getPreparationTime()).thenReturn(25);
        when(mockRecipe.getSteps()).thenReturn("Moler cafe y extraer");
        when(mockRecipe.getTips()).thenReturn("");
        when(mockRecipe.getCupping()).thenReturn("");
        when(mockRecipe.getGrindSize()).thenReturn("fine");
        when(mockRecipe.getCreatedAt()).thenReturn(new Date());
        when(shared.preparationContextFacade.createRecipe(any())).thenReturn(Optional.of(mockRecipe));
        when(shared.preparationContextFacade.getIngredientsByRecipeId(anyLong())).thenReturn(List.of());
    }

    @Given("un barista autenticado con perfil id {int} pero creación de receta falla")
    public void unBaristaAutenticadoPeroCreacionDeRecetaFalla(int profileId) {
        when(shared.currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of((long) profileId));
        when(shared.preparationContextFacade.createRecipe(any())).thenReturn(Optional.empty());
    }

    @When("envía una solicitud para crear una receta con nombre {string} metodo {string} ratio {string} tiempo {int}")
    public void enviaSolicitudCrearReceta(String nombre, String metodo, String ratio, int tiempo) throws Exception {
        var body = Map.of(
                "name", nombre,
                "imageUrl", "http://example.com/img",
                "extractionMethod", metodo,
                "extractionCategory", "coffee",
                "ratio", ratio,
                "preparationTime", tiempo,
                "steps", "Moler cafe y extraer"
        );
        shared.lastResult = mockMvc.perform(post("/api/v1/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    @And("la respuesta contiene el id de la receta creada")
    public void laRespuestaContieneElIdDeLaRecetaCreada() throws Exception {
        shared.lastResult.andExpect(jsonPath("$.id").isNotEmpty());
    }
}
