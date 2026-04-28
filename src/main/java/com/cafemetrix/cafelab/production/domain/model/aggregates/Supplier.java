package com.cafemetrix.cafelab.production.domain.model.aggregates;

import com.cafemetrix.cafelab.production.domain.model.commands.CreateSupplierCommand;
import com.cafemetrix.cafelab.production.domain.model.commands.UpdateSupplierCommand;
import com.cafemetrix.cafelab.production.domain.model.valueobjects.SupplierEmail;
import com.cafemetrix.cafelab.production.domain.model.valueobjects.SupplierLocation;
import com.cafemetrix.cafelab.production.domain.model.valueobjects.SupplierName;
import com.cafemetrix.cafelab.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/** Proveedor; {@code userId} persiste en {@code user_id} (FK a profiles.id). */
@Entity
@Table(name = "suppliers")
public class Supplier extends AuditableAbstractAggregateRoot<Supplier> {

    @Getter
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "name", length = 100))
    private SupplierName name;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email", length = 100))
    private SupplierEmail email;

    @Column(name = "phone", nullable = false)
    private Long phone;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "location", length = 200))
    private SupplierLocation location;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "supplier_specialties", joinColumns = @JoinColumn(name = "supplier_id"))
    @Column(name = "specialty", length = 100)
    private List<String> specialties = new ArrayList<>();

    
    public Supplier() {}

    
    public Supplier(Long userId, String name, String email, Long phone, String location, List<String> specialties) {
        this.userId = userId;
        this.name = new SupplierName(name);
        this.email = new SupplierEmail(email);
        this.phone = phone;
        this.location = new SupplierLocation(location);
        this.specialties = specialties != null ? new ArrayList<>(specialties) : new ArrayList<>();
    }

    
    public Supplier(CreateSupplierCommand command) {
        this.userId = command.userId();
        this.name = new SupplierName(command.name());
        this.email = new SupplierEmail(command.email());
        this.phone = command.phone();
        this.location = new SupplierLocation(command.location());
        this.specialties = command.specialties() != null ? new ArrayList<>(command.specialties()) : new ArrayList<>();
    }

    
    public Supplier update(UpdateSupplierCommand command) {
        this.name = new SupplierName(command.name());
        this.email = new SupplierEmail(command.email());
        this.phone = command.phone();
        this.location = new SupplierLocation(command.location());
        this.specialties = command.specialties() != null ? new ArrayList<>(command.specialties()) : new ArrayList<>();
        return this;
    }

    public String getName() { return name.value(); }
    public String getEmail() { return email.value(); }
    public Long getPhone() { return phone; }
    public String getLocation() { return location.value(); }
    public List<String> getSpecialties() { return new ArrayList<>(specialties); }
}
