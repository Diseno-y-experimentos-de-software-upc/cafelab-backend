package com.cafemetrix.cafelab.management.domain.model.aggregates;

import com.cafemetrix.cafelab.management.domain.model.commands.CreateInventoryEntryCommand;
import com.cafemetrix.cafelab.management.domain.model.commands.UpdateInventoryEntryCommand;
import com.cafemetrix.cafelab.management.domain.model.valueobjects.*;
import com.cafemetrix.cafelab.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

/** Entrada de inventario; {@code userId} persiste en {@code user_id} (FK a profiles.id). */
@Entity
@SQLRestriction("deleted_at IS NULL")
@Table(name = "inventory_entries")
public class InventoryEntry extends AuditableAbstractAggregateRoot<InventoryEntry> {

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
    @Column(name = "motivo_de_consumo", nullable = false, length = 50)
    private String motivoDeConsumo;

    @Getter
    @Column(name = "notas_de_uso", columnDefinition = "TEXT")
    private String notasDeUso;

    public InventoryEntry() {}

    public InventoryEntry(Long userId, Long coffeeLotId, Double quantityUsed, 
                         LocalDateTime dateUsed, String finalProduct, String motivoDeConsumo, String notasDeUso) {
        this.userId = userId;
        this.coffeeLotId = coffeeLotId;
        this.quantityUsed = quantityUsed;
        this.dateUsed = dateUsed;
        this.finalProduct = new FinalProduct(finalProduct);
        this.motivoDeConsumo = motivoDeConsumo;
        this.notasDeUso = notasDeUso;
    }

    public InventoryEntry(CreateInventoryEntryCommand command) {
        validateDateUsed(command.dateUsed());
        this.userId = command.userId();
        this.coffeeLotId = command.coffeeLotId();
        this.quantityUsed = command.quantityUsed();
        this.dateUsed = command.dateUsed();
        this.finalProduct = new FinalProduct(command.finalProduct());
        this.motivoDeConsumo = command.motivoDeConsumo();
        this.notasDeUso = command.notasDeUso();
    }

    public InventoryEntry update(UpdateInventoryEntryCommand command) {
        this.coffeeLotId = command.coffeeLotId();
        this.quantityUsed = command.quantityUsed();
        this.dateUsed = command.dateUsed();
        this.finalProduct = new FinalProduct(command.finalProduct());
        this.motivoDeConsumo = command.motivoDeConsumo();
        this.notasDeUso = command.notasDeUso();
        return this;
    }


    private void validateDateUsed(LocalDateTime dateUsed) {
        if (dateUsed == null) {
            throw new IllegalArgumentException("La fecha de uso es requerida");
        }

        if (dateUsed.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha de uso no puede ser anterior a un año desde hoy");
        }

        if (dateUsed.isAfter(LocalDateTime.now().plusYears(5))) {
            throw new IllegalArgumentException("La fecha de uso no puede ser posterior a 5 años desde hoy");
        }
    }

    public Double getQuantityUsed() { return quantityUsed; }
    public LocalDateTime getDateUsed() { return dateUsed; }
    public String getFinalProduct() { return finalProduct.value(); }
}
