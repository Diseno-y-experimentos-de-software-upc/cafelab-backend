package com.cafemetrix.cafelab.preparation.application.internal.commandservices;

import com.cafemetrix.cafelab.cuppingsessions.domain.model.queries.GetCuppingSessionByIdForUserQuery;
import com.cafemetrix.cafelab.cuppingsessions.domain.services.CuppingSessionQueryService;
import com.cafemetrix.cafelab.preparation.domain.model.aggregates.Recipe;
import com.cafemetrix.cafelab.preparation.domain.model.commands.CreateRecipeCommand;
import com.cafemetrix.cafelab.preparation.domain.model.commands.DeleteRecipeCommand;
import com.cafemetrix.cafelab.preparation.domain.model.commands.UpdateRecipeCommand;
import com.cafemetrix.cafelab.preparation.domain.services.RecipeCommandService;
import com.cafemetrix.cafelab.preparation.infrastructure.persistence.jpa.repositories.IngredientRepository;
import com.cafemetrix.cafelab.preparation.infrastructure.persistence.jpa.repositories.PortfolioRepository;
import com.cafemetrix.cafelab.preparation.infrastructure.persistence.jpa.repositories.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RecipeCommandServiceImpl implements RecipeCommandService {
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final PortfolioRepository portfolioRepository;
    private final CuppingSessionQueryService cuppingSessionQueryService;

    public RecipeCommandServiceImpl(
            RecipeRepository recipeRepository,
            IngredientRepository ingredientRepository,
            PortfolioRepository portfolioRepository,
            CuppingSessionQueryService cuppingSessionQueryService) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.portfolioRepository = portfolioRepository;
        this.cuppingSessionQueryService = cuppingSessionQueryService;
    }

    @Override
    public Optional<Recipe> handle(CreateRecipeCommand command) {
        if (recipeRepository.existsByNameValueAndUserId(command.name(), command.userId())) {
            throw new IllegalArgumentException(
                    "Ya existe una receta con el nombre \"" + command.name() + "\". Use un nombre diferente.");
        }
        assertCuppingSessionBelongsToUser(command.userId(), command.cuppingSessionId());
        assertPortfolioBelongsToUser(command.userId(), command.portfolioId());
        try {
            var recipe = new Recipe(command);
            return Optional.of(recipeRepository.save(recipe));
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "No se pudo guardar la receta. Revise los datos (nombre, pasos, tiempos) e intente de nuevo.",
                    e);
        }
    }

    @Override
    public Optional<Recipe> handle(UpdateRecipeCommand command) {
        var existing = recipeRepository.findByIdAndUserId(command.recipeId(), command.userId());
        if (existing.isEmpty()) {
            return Optional.empty();
        }
        if (recipeRepository.existsByNameAndUserIdExcluding(command.name(), command.userId(), command.recipeId())) {
            throw new IllegalArgumentException(
                    "Ya existe una receta con el nombre \"" + command.name() + "\". Use un nombre diferente.");
        }
        assertCuppingSessionBelongsToUser(command.userId(), command.cuppingSessionId());
        assertPortfolioBelongsToUser(command.userId(), command.portfolioId());
        try {
            var recipe = existing.get();
            recipe.update(command);
            return Optional.of(recipeRepository.save(recipe));
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "No se pudo actualizar la receta. Revise los datos e intente de nuevo.", e);
        }
    }

    @Override
    public boolean handle(DeleteRecipeCommand command) {
        var existing = recipeRepository.findByIdAndUserId(command.recipeId(), command.userId());
        if (existing.isEmpty()) {
            return false;
        }
        existing.get().softDelete();
        recipeRepository.save(existing.get());
        return true;
    }

    private void assertCuppingSessionBelongsToUser(Long userId, Long cuppingSessionId) {
        if (cuppingSessionId == null) {
            return;
        }
        if (cuppingSessionQueryService
                .handle(new GetCuppingSessionByIdForUserQuery(cuppingSessionId, userId))
                .isEmpty()) {
            throw new IllegalArgumentException(
                    "La sesión de cata indicada no existe o no pertenece a su perfil. Elija otra o deje \"Sin sesión de cata\".");
        }
    }

    private void assertPortfolioBelongsToUser(Long userId, Long portfolioId) {
        if (portfolioId == null) {
            return;
        }
        if (portfolioRepository.findByIdAndUserId(portfolioId, userId).isEmpty()) {
            throw new IllegalArgumentException(
                    "El portafolio indicado no existe o no pertenece a su perfil. Elija otro o deje \"Sin portafolio\".");
        }
    }
}
