package com.cafemetrix.cafelab.defects.domain.model.aggregates;

import com.cafemetrix.cafelab.defects.domain.model.commands.CreateDefectCommand;
import com.cafemetrix.cafelab.defects.domain.model.commands.UpdateDefectCommand;
import com.cafemetrix.cafelab.defects.domain.model.valueobjects.DefectName;
import com.cafemetrix.cafelab.defects.domain.model.valueobjects.DefectType;
import com.cafemetrix.cafelab.defects.domain.model.valueobjects.ProbableCause;
import com.cafemetrix.cafelab.defects.domain.model.valueobjects.SuggestedSolution;
import com.cafemetrix.cafelab.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@SQLRestriction("deleted_at IS NULL")
public class Defect extends AuditableAbstractAggregateRoot<Defect> {

    @Getter
    @Column(name = "profile_id")
    private Long userId;

    @Getter
    @Column(name = "coffee_display_name")
    private String coffeeDisplayName;

    @Getter
    @Column(name = "coffee_region")
    private String coffeeRegion;

    @Getter
    @Column(name = "coffee_variety")
    private String coffeeVariety;

    @Getter
    @Column(name = "coffee_total_weight")
    private Double coffeeTotalWeight;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "name"))
    private DefectName name;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "defect_type"))
    private DefectType defectType;

    @Getter
    @Column(nullable = false)
    private Double defectWeight;

    @Getter
    @Column(nullable = false)
    private Double percentage;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "probable_cause"))
    private ProbableCause probableCause;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "suggested_solution"))
    private SuggestedSolution suggestedSolution;

    public Defect() {}

    public Defect(CreateDefectCommand command) {
        this.userId = command.userId();
        this.coffeeDisplayName = command.coffeeDisplayName().trim();
        this.coffeeRegion = command.coffeeRegion();
        this.coffeeVariety = command.coffeeVariety();
        this.coffeeTotalWeight = command.coffeeTotalWeight();
        this.name = new DefectName(command.name());
        this.defectType = new DefectType(command.defectType());
        this.defectWeight = command.defectWeight();
        this.percentage = command.percentage();
        this.probableCause = new ProbableCause(command.probableCause());
        this.suggestedSolution = new SuggestedSolution(command.suggestedSolution());
    }

    public String getName() {
        return name.value();
    }

    public String getDefectType() {
        return defectType.value();
    }

    public String getProbableCause() {
        return probableCause.value();
    }

    public String getSuggestedSolution() {
        return suggestedSolution.value();
    }

    /** Actualiza todos los campos editables (mismo contrato que alta). */
    public void applyUpdate(UpdateDefectCommand command) {
        this.coffeeDisplayName = command.coffeeDisplayName().trim();
        this.coffeeRegion = command.coffeeRegion();
        this.coffeeVariety = command.coffeeVariety() != null ? command.coffeeVariety().trim() : null;
        this.coffeeTotalWeight = command.coffeeTotalWeight();
        this.name = new DefectName(command.name());
        this.defectType = new DefectType(command.defectType());
        this.defectWeight = command.defectWeight();
        this.percentage = command.percentage();
        this.probableCause = new ProbableCause(command.probableCause());
        this.suggestedSolution = new SuggestedSolution(command.suggestedSolution());
    }
}
