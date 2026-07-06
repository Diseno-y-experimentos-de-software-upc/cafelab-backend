package com.cafemetrix.cafelab.production.domain.model.aggregates;

import com.cafemetrix.cafelab.production.domain.model.commands.CreateCoffeeLotCommand;
import com.cafemetrix.cafelab.production.domain.model.commands.CreateCoffeeLotVersionCommand;
import com.cafemetrix.cafelab.production.domain.model.valueobjects.*;
import com.cafemetrix.cafelab.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

/** Lote; {@code userId} persiste en {@code user_id} (FK a profiles.id). */
@Entity
@SQLRestriction("deleted_at IS NULL")
@Table(name = "coffee_lots")
public class CoffeeLot extends AuditableAbstractAggregateRoot<CoffeeLot> {

    public static final String RECORD_STATUS_ACTIVE = "activo";
    public static final String RECORD_STATUS_ANNULLED = "anulado";
    public static final int ANNULMENT_REASON_MAX_LENGTH = 25;

    @Getter
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Getter
    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;

    @Getter
    @Column(name = "supplier_name", nullable = false, length = 100)
    private String supplierName;

    @Getter
    @Column(name = "lot_lineage_id", nullable = false)
    private Long lotLineageId;

    @Getter
    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Getter
    @Column(name = "is_current", nullable = false)
    private Boolean isCurrent;

    @Getter
    @Column(name = "supersedes_id")
    private Long supersedesId;

    @Getter
    @Column(name = "record_status", nullable = false, length = 20)
    private String recordStatus;

    @Getter
    @Column(name = "annulment_reason", nullable = false, length = 25)
    private String annulmentReason;

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

    @Column(name = "original_weight", nullable = false)
    private Double originalWeight;

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

    public CoffeeLot(CreateCoffeeLotCommand command, String supplierName) {
        if (supplierName == null || supplierName.isBlank()) {
            throw new IllegalArgumentException("SupplierName es requerido");
        }
        this.userId = command.userId();
        this.supplierId = command.supplierId();
        this.supplierName = supplierName.trim();
        this.lotName = new CoffeeLotName(command.lotName());
        this.coffeeType = new CoffeeType(command.coffeeType());
        this.processingMethod = new ProcessingMethod(command.processingMethod());
        this.altitude = command.altitude();
        this.weight = command.weight();
        this.originalWeight = command.weight();
        this.origin = new Origin(command.origin());
        this.status = new CoffeeLotStatus(command.status());
        this.certifications = command.certifications() != null ? new ArrayList<>(command.certifications()) : new ArrayList<>();
        this.recordStatus = RECORD_STATUS_ACTIVE;
        this.annulmentReason = "";
    }

    public void initializeAsFirstVersion(Long lineageId) {
        this.lotLineageId = lineageId;
        this.versionNumber = 1;
        this.isCurrent = true;
        this.supersedesId = null;
        if (this.recordStatus == null) {
            this.recordStatus = RECORD_STATUS_ACTIVE;
        }
        if (this.annulmentReason == null) {
            this.annulmentReason = "";
        }
    }

    public void markAsHistorical() {
        this.isCurrent = false;
    }

    public CoffeeLot createSuccessor(CreateCoffeeLotVersionCommand command) {
        CoffeeLot next = new CoffeeLot();
        next.userId = this.userId;
        next.supplierId = this.supplierId;
        next.supplierName = this.supplierName;
        next.lotLineageId = this.lotLineageId;
        next.versionNumber = this.versionNumber + 1;
        next.isCurrent = true;
        next.supersedesId = this.getId();
        next.weight = this.weight;
        next.originalWeight = this.originalWeight;
        next.lotName = new CoffeeLotName(command.lotName());
        next.coffeeType = new CoffeeType(command.coffeeType());
        next.processingMethod = new ProcessingMethod(command.processingMethod());
        next.altitude = command.altitude();
        next.origin = new Origin(command.origin());
        next.status = new CoffeeLotStatus(command.status());
        next.certifications = command.certifications() != null
                ? new ArrayList<>(command.certifications())
                : new ArrayList<>();
        next.recordStatus = this.recordStatus;
        next.annulmentReason = this.annulmentReason;
        return next;
    }

    public void annull(String reasonText) {
        String safe = reasonText == null ? "" : reasonText.trim();
        if (safe.length() > ANNULMENT_REASON_MAX_LENGTH) {
            safe = safe.substring(0, ANNULMENT_REASON_MAX_LENGTH);
        }
        if (safe.isEmpty()) {
            safe = RECORD_STATUS_ANNULLED;
        }
        this.recordStatus = RECORD_STATUS_ANNULLED;
        this.annulmentReason = safe;
    }

    public boolean isAnnulled() {
        return RECORD_STATUS_ANNULLED.equals(this.recordStatus);
    }

    public void adjustCurrentWeight(double newWeight) {
        if (newWeight < 0) {
            throw new IllegalArgumentException("El peso actual no puede ser negativo");
        }
        this.weight = newWeight;
    }

    public String getLotName() { return lotName.value(); }
    public String getCoffeeType() { return coffeeType.value(); }
    public String getProcessingMethod() { return processingMethod.value(); }
    public Integer getAltitude() { return altitude; }
    public Double getWeight() { return weight; }
    public Double getOriginalWeight() { return originalWeight; }
    public String getOrigin() { return origin.value(); }
    public String getStatus() { return status.value(); }
    public List<String> getCertifications() { return new ArrayList<>(certifications); }
}
