package com.cafemetrix.cafelab.management.domain.model.aggregates;

import com.cafemetrix.cafelab.management.domain.model.commands.CreateProductionCostRecordCommand;
import com.cafemetrix.cafelab.management.domain.model.commands.UpdateProductionCostRecordCommand;
import com.cafemetrix.cafelab.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;


@Entity
@Table(name = "production_cost_records")
@Getter
public class ProductionCostRecord extends AuditableAbstractAggregateRoot<ProductionCostRecord> {

    /** Valor del campo {@code status} para registros válidos. */
    public static final String STATUS_REGISTERED = "registrado";
    /** Valor del campo {@code status} para registros anulados (no se eliminan: quedan para auditoría). */
    public static final String STATUS_ANNULLED = "anulado";

    /** Tope de caracteres del campo {@code reason} (motivo de anulación). */
    public static final int REASON_MAX_LENGTH = 25;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "coffee_lot_id", nullable = false)
    private Long coffeeLotId;

    @Column(name = "lot_name", nullable = false, length = 150)
    private String lotName;

    @Column(name = "coffee_type", nullable = false, length = 80)
    private String coffeeType;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "total_kg", nullable = false)
    private Double totalKg;

    @Column(name = "margin_percent", nullable = false)
    private Double marginPercent;

    @Column(name = "raw_materials_cost", nullable = false)
    private Double rawMaterialsCost;

    @Column(name = "labor_cost", nullable = false)
    private Double laborCost;

    @Column(name = "transport_cost", nullable = false)
    private Double transportCost;

    @Column(name = "storage_cost", nullable = false)
    private Double storageCost;

    @Column(name = "processing_cost", nullable = false)
    private Double processingCost;

    @Column(name = "other_indirect_costs", nullable = false)
    private Double otherIndirectCosts;

    @Column(name = "total_direct_cost", nullable = false)
    private Double totalDirectCost;

    @Column(name = "total_indirect_cost", nullable = false)
    private Double totalIndirectCost;

    @Column(name = "total_cost", nullable = false)
    private Double totalCost;

    @Column(name = "cost_per_kg", nullable = false)
    private Double costPerKg;

    @Column(name = "suggested_price", nullable = false)
    private Double suggestedPrice;

    @Column(name = "potential_margin", nullable = false)
    private Double potentialMargin;

    /** Estado del registro: {@code registrado} (por defecto) o {@code anulado}. */
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    /**
     * Motivo asociado al estado.
     * <ul>
     *   <li>Mientras el estado sea {@code registrado}, vale literalmente {@code "registrado"}.</li>
     *   <li>Cuando se anula, contiene el motivo elegido por el usuario o un texto libre limitado a
     *       {@link #REASON_MAX_LENGTH} caracteres (caso «otro»).</li>
     * </ul>
     */
    @Column(name = "reason", nullable = false, length = REASON_MAX_LENGTH)
    private String reason;

    public ProductionCostRecord() {}

    public ProductionCostRecord(CreateProductionCostRecordCommand c) {
        this.userId = c.userId();
        this.coffeeLotId = c.coffeeLotId();
        this.lotName = c.lotName();
        this.coffeeType = c.coffeeType();
        this.currency = c.currency();
        this.totalKg = c.totalKg();
        this.marginPercent = c.marginPercent();
        this.rawMaterialsCost = c.rawMaterialsCost();
        this.laborCost = c.laborCost();
        this.transportCost = c.transportCost();
        this.storageCost = c.storageCost();
        this.processingCost = c.processingCost();
        this.otherIndirectCosts = c.otherIndirectCosts();
        this.totalDirectCost = c.totalDirectCost();
        this.totalIndirectCost = c.totalIndirectCost();
        this.totalCost = c.totalCost();
        this.costPerKg = c.costPerKg();
        this.suggestedPrice = c.suggestedPrice();
        this.potentialMargin = c.potentialMargin();
        this.status = STATUS_REGISTERED;
        this.reason = STATUS_REGISTERED;
    }

    public ProductionCostRecord update(UpdateProductionCostRecordCommand c) {
        this.coffeeLotId = c.coffeeLotId();
        this.lotName = c.lotName();
        this.coffeeType = c.coffeeType();
        this.currency = c.currency();
        this.totalKg = c.totalKg();
        this.marginPercent = c.marginPercent();
        this.rawMaterialsCost = c.rawMaterialsCost();
        this.laborCost = c.laborCost();
        this.transportCost = c.transportCost();
        this.storageCost = c.storageCost();
        this.processingCost = c.processingCost();
        this.otherIndirectCosts = c.otherIndirectCosts();
        this.totalDirectCost = c.totalDirectCost();
        this.totalIndirectCost = c.totalIndirectCost();
        this.totalCost = c.totalCost();
        this.costPerKg = c.costPerKg();
        this.suggestedPrice = c.suggestedPrice();
        this.potentialMargin = c.potentialMargin();
        return this;
    }

    /**
     * Marca el registro como anulado y persiste el motivo provisto. El motivo se trunca a
     * {@link #REASON_MAX_LENGTH} caracteres por defensa, ya que la columna también lo está.
     * Operación idempotente: anular un registro ya anulado actualiza el motivo.
     */
    public ProductionCostRecord annull(String reasonText) {
        String safe = reasonText == null ? "" : reasonText.trim();
        if (safe.length() > REASON_MAX_LENGTH) {
            safe = safe.substring(0, REASON_MAX_LENGTH);
        }
        if (safe.isEmpty()) {
            safe = "anulado";
        }
        this.status = STATUS_ANNULLED;
        this.reason = safe;
        return this;
    }

    public boolean isAnnulled() {
        return STATUS_ANNULLED.equals(this.status);
    }
}
