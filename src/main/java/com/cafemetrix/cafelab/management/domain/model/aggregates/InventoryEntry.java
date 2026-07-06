package com.cafemetrix.cafelab.management.domain.model.aggregates;

import com.cafemetrix.cafelab.management.domain.model.commands.CreateInventoryEntryCommand;
import com.cafemetrix.cafelab.management.domain.model.commands.UpdateInventoryEntryCommand;
import com.cafemetrix.cafelab.management.domain.model.valueobjects.*;
import com.cafemetrix.cafelab.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/** Entrada de inventario; {@code userId} persiste en {@code user_id} (FK a profiles.id). */
@Entity
@SQLRestriction("deleted_at IS NULL")
@Table(name = "inventory_entries")
public class InventoryEntry extends AuditableAbstractAggregateRoot<InventoryEntry> {

    /** Zona de negocio para validar "consumo del día de hoy" con el calendario local del usuario. */
    private static final ZoneId BUSINESS_ZONE = ZoneId.of("America/Lima");

    @Getter
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Getter
    @Column(name = "coffee_lot_id", nullable = false)
    private Long coffeeLotId;

    @Column(name = "quantity_used", nullable = false)
    private Double quantityUsed;

    @Column(name = "date_used", nullable = false)
    private LocalDateTime dateUsed;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "final_product", length = 100))
    private FinalProduct finalProduct;

    @Getter
    @Column(name = "consumption_reason", nullable = false, length = 50)
    private String consumptionReason;

    @Getter
    @Column(name = "usage_notes", columnDefinition = "TEXT")
    private String usageNotes;

    public InventoryEntry() {}

    public InventoryEntry(Long userId, Long coffeeLotId, Double quantityUsed,
                         LocalDateTime dateUsed, String finalProduct,
                         String consumptionReason, String usageNotes) {
        this.userId = userId;
        this.coffeeLotId = coffeeLotId;
        this.quantityUsed = quantityUsed;
        this.dateUsed = dateUsed;
        this.finalProduct = new FinalProduct(finalProduct);
        this.consumptionReason = consumptionReason;
        this.usageNotes = usageNotes;
    }

    public InventoryEntry(Long userId, Long coffeeLotId, Double quantityUsed,
                         LocalDateTime dateUsed, String finalProduct) {
        this(userId, coffeeLotId, quantityUsed, dateUsed, finalProduct, "other", null);
    }

    public InventoryEntry(CreateInventoryEntryCommand command) {
        validateDateUsed(command.dateUsed());
        this.userId = command.userId();
        this.coffeeLotId = command.coffeeLotId();
        this.quantityUsed = command.quantityUsed();
        this.dateUsed = command.dateUsed();
        this.finalProduct = new FinalProduct(command.finalProduct());
        this.consumptionReason = command.consumptionReason();
        this.usageNotes = command.usageNotes();
    }

    public InventoryEntry update(UpdateInventoryEntryCommand command) {
        validateDateUsed(command.dateUsed());
        this.coffeeLotId = command.coffeeLotId();
        this.quantityUsed = command.quantityUsed();
        this.dateUsed = command.dateUsed();
        this.finalProduct = new FinalProduct(command.finalProduct());
        this.consumptionReason = command.consumptionReason();
        this.usageNotes = command.usageNotes();
        return this;
    }


    private void validateDateUsed(LocalDateTime dateUsed) {
        if (dateUsed == null) {
            throw new IllegalArgumentException("La fecha de uso es requerida");
        }

        LocalDate today = LocalDate.now(BUSINESS_ZONE);
        if (!dateUsed.toLocalDate().isEqual(today)) {
            throw new IllegalArgumentException("Solo se puede registrar consumo del dia de hoy");
        }
    }

    public Double getQuantityUsed() { return quantityUsed; }
    public LocalDateTime getDateUsed() { return dateUsed; }
    public String getFinalProduct() { return finalProduct.value(); }
}
