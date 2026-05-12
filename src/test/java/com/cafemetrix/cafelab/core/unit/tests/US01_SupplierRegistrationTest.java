package com.cafemetrix.cafelab.core.unit.tests;

import com.cafemetrix.cafelab.production.domain.model.aggregates.Supplier;
import com.cafemetrix.cafelab.production.domain.model.commands.CreateSupplierCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("US01 - Registro de Proveedores")
class US01_SupplierRegistrationTest {

    @Test
    @DisplayName("crea un proveedor con datos de evaluacion y especialidades")
    void createsSupplierWithSpecialties() {
        var supplier = new Supplier(new CreateSupplierCommand(
                7L, "Finca El Cedro", "cedro@example.com", 987654321L,
                "Cajamarca", List.of("washed", "honey")));

        assertThat(supplier.getUserId()).isEqualTo(7L);
        assertThat(supplier.getName()).isEqualTo("Finca El Cedro");
        assertThat(supplier.getEmail()).isEqualTo("cedro@example.com");
        assertThat(supplier.getSpecialties()).containsExactly("washed", "honey");
    }

    @Test
    @DisplayName("rechaza especialidades repetidas para mantener evaluacion consistente")
    void rejectsDuplicatedSpecialties() {
        assertThatThrownBy(() -> new CreateSupplierCommand(
                7L, "Finca El Cedro", "cedro@example.com", 987654321L,
                "Cajamarca", List.of("Washed", " washed ")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("especialidades");
    }
}
