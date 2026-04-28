package com.cafemetrix.cafelab.production.domain.model.aggregates;

import com.cafemetrix.cafelab.production.domain.model.commands.CreateCoffeeLotCommand;
import com.cafemetrix.cafelab.production.domain.model.commands.UpdateCoffeeLotCommand;
import com.cafemetrix.cafelab.production.domain.model.valueobjects.*;
import com.cafemetrix.cafelab.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/** Lote; {@code userId} persiste en {@code user_id} (FK a profiles.id). */
@Entity
@Table(name = "coffee_lots")
public class CoffeeLot extends AuditableAbstractAggregateRoot<CoffeeLot> {

    @Getter
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Getter
    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "lot_name", length = 100))
    private CoffeeLotName lotName;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "coffee_type", length = 50))
    private CoffeeType coffeeType;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "processing_method", length = 50))
    private ProcessingMethod processingMethod;

    @Column(name = "altitude", nullable = false)
    private Integer altitude;

    @Column(name = "weight", nullable = false)
    private Double weight;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "origin", length = 100))
    private Origin origin;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "status", length = 20))
    private CoffeeLotStatus status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "coffee_lot_certifications", joinColumns = @JoinColumn(name = "coffee_lot_id"))
    @Column(name = "certification", length = 100)
    private List<String> certifications = new ArrayList<>();

    
    public CoffeeLot() {}

    
    public CoffeeLot(Long userId, Long supplierId, String lotName, String coffeeType, 
                    String processingMethod, Integer altitude, Double weight, 
                    String origin, String status, List<String> certifications) {
        this.userId = userId;
        this.supplierId = supplierId;
        this.lotName = new CoffeeLotName(lotName);
        this.coffeeType = new CoffeeType(coffeeType);
        this.processingMethod = new ProcessingMethod(processingMethod);
        this.altitude = altitude;
        this.weight = weight;
        this.origin = new Origin(origin);
        this.status = new CoffeeLotStatus(status);
        this.certifications = certifications != null ? new ArrayList<>(certifications) : new ArrayList<>();
    }

    
    public CoffeeLot(CreateCoffeeLotCommand command) {
        this.userId = command.userId();
        this.supplierId = command.supplierId();
        this.lotName = new CoffeeLotName(command.lotName());
        this.coffeeType = new CoffeeType(command.coffeeType());
        this.processingMethod = new ProcessingMethod(command.processingMethod());
        this.altitude = command.altitude();
        this.weight = command.weight();
        this.origin = new Origin(command.origin());
        this.status = new CoffeeLotStatus(command.status());
        this.certifications = command.certifications() != null ? new ArrayList<>(command.certifications()) : new ArrayList<>();
    }

    
    public CoffeeLot update(UpdateCoffeeLotCommand command) {
        this.lotName = new CoffeeLotName(command.lotName());
        this.coffeeType = new CoffeeType(command.coffeeType());
        this.processingMethod = new ProcessingMethod(command.processingMethod());
        this.altitude = command.altitude();
        this.weight = command.weight();
        this.origin = new Origin(command.origin());
        this.status = new CoffeeLotStatus(command.status());
        this.certifications = command.certifications() != null ? new ArrayList<>(command.certifications()) : new ArrayList<>();
        return this;
    }

    public String getLotName() { return lotName.value(); }
    public String getCoffeeType() { return coffeeType.value(); }
    public String getProcessingMethod() { return processingMethod.value(); }
    public Integer getAltitude() { return altitude; }
    public Double getWeight() { return weight; }
    public String getOrigin() { return origin.value(); }
    public String getStatus() { return status.value(); }
    public List<String> getCertifications() { return new ArrayList<>(certifications); }
}
