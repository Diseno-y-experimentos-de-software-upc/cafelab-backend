package com.cafemetrix.cafelab.preparation.domain.model.aggregates;

import com.cafemetrix.cafelab.preparation.domain.model.commands.CreateRecipeCommand;
import com.cafemetrix.cafelab.preparation.domain.model.commands.UpdateRecipeCommand;
import com.cafemetrix.cafelab.preparation.domain.model.valueobjects.*;
import com.cafemetrix.cafelab.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.SQLRestriction;

/**
 * Receta de preparación; {@code userId} persiste en columna {@code user_id} (FK a profiles.id).
 */
@Entity
@SQLRestriction("deleted_at IS NULL")
@Table(name = "recipes", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "user_id"}))
public class Recipe extends AuditableAbstractAggregateRoot<Recipe> {

    @Getter
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "name", length = 20))
    private RecipeName name;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "extraction_method", nullable = false)
    private ExtractionMethod extractionMethod;

    @Column(name = "extraction_category", nullable = false)
    private ExtractionCategory extractionCategory;

    @Column(name = "ratio", length = 10, nullable = false)
    private String ratio;

    @Getter
    @Column(name = "cupping_session_id")
    private Long cuppingSessionId;

    @Getter
    @Column(name = "portfolio_id")
    private Long portfolioId;

    @Column(name = "preparation_time", nullable = false)
    private Integer preparationTime;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String steps;

    @Column(columnDefinition = "TEXT")
    private String tips;

    @Column(length = 20, nullable = false)
    private String cupping;

    @Column(name = "grind_size", length = 20, nullable = false)
    private String grindSize;

    public Recipe() {}

    public Recipe(
            Long userId,
            String name,
            String imageUrl,
            String extractionMethod,
            String extractionCategory,
            String ratio,
            Long cuppingSessionId,
            Long portfolioId,
            Integer preparationTime,
            String steps,
            String tips,
            String cupping,
            String grindSize) {
        this.userId = userId;
        this.name = new RecipeName(name);
        this.imageUrl = imageUrl;
        this.extractionMethod = ExtractionMethod.fromString(extractionMethod);
        this.extractionCategory = ExtractionCategory.fromString(extractionCategory);
        this.ratio = ratio;
        this.cuppingSessionId = cuppingSessionId;
        this.portfolioId = portfolioId;
        this.preparationTime = preparationTime;
        this.steps = steps;
        this.tips = tips;
        this.cupping = cupping;
        this.grindSize = grindSize;
    }

    public Recipe(CreateRecipeCommand command) {
        this.userId = command.userId();
        this.name = new RecipeName(command.name());
        this.imageUrl = command.imageUrl();
        this.extractionMethod = ExtractionMethod.fromString(command.extractionMethod());
        this.extractionCategory = ExtractionCategory.fromString(command.extractionCategory());
        this.ratio = command.ratio();
        this.cuppingSessionId = command.cuppingSessionId();
        this.portfolioId = command.portfolioId();
        this.preparationTime = command.preparationTime();
        this.steps = command.steps();
        this.tips = command.tips();
        this.cupping = command.cupping();
        this.grindSize = command.grindSize();
    }

    public Recipe update(UpdateRecipeCommand command) {
        this.name = new RecipeName(command.name());
        this.imageUrl = command.imageUrl();
        this.extractionMethod = ExtractionMethod.fromString(command.extractionMethod());
        this.extractionCategory = ExtractionCategory.fromString(command.extractionCategory());
        this.ratio = command.ratio();
        this.cuppingSessionId = command.cuppingSessionId();
        this.portfolioId = command.portfolioId();
        this.preparationTime = command.preparationTime();
        this.steps = command.steps();
        this.tips = command.tips();
        this.cupping = command.cupping();
        this.grindSize = command.grindSize();
        return this;
    }

    public String getName() {
        return name.value();
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public ExtractionMethod getExtractionMethod() {
        return extractionMethod;
    }

    public ExtractionCategory getExtractionCategory() {
        return extractionCategory;
    }

    public String getRatio() {
        return ratio;
    }

    public Integer getPreparationTime() {
        return preparationTime;
    }

    public String getSteps() {
        return steps;
    }

    public String getTips() {
        return tips;
    }

    public String getCupping() {
        return cupping;
    }

    public String getGrindSize() {
        return grindSize;
    }
}
