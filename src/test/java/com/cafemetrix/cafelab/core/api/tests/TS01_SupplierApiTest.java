package com.cafemetrix.cafelab.core.api.tests;

import com.cafemetrix.cafelab.iam.infrastructure.authorization.sfs.support.CurrentProfileIdResolver;
import com.cafemetrix.cafelab.production.interfaces.acl.CoffeeproductionContextFacade;
import com.cafemetrix.cafelab.production.interfaces.rest.SuppliersController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SuppliersController.class, 
            excludeAutoConfiguration = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
@DisplayName("TS01 - API Proveedores")
class TS01_SupplierApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CoffeeproductionContextFacade coffeeproductionContextFacade;

    @MockitoBean
    private CurrentProfileIdResolver currentProfileIdResolver;

    @Test
    @DisplayName("POST /api/v1/suppliers crea un proveedor exitosamente (201)")
    void createSupplierReturnsCreated() throws Exception {
        when(currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of(1L));
        when(coffeeproductionContextFacade.createSupplier(any(), any(), any(), any(), any(), any()))
                .thenReturn(1L);
        
        var supplier = new com.cafemetrix.cafelab.production.domain.model.aggregates.Supplier(
                new com.cafemetrix.cafelab.production.domain.model.commands.CreateSupplierCommand(
                        7L, "Finca Test", "test@test.com", 987654321L, "Jaen", List.of()
                ));
        
        // Asignar ID manualmente mediante reflexión para evitar el NPE en el controlador
        java.lang.reflect.Field idField = com.cafemetrix.cafelab.shared.domain.model.aggregates.AuditableAbstractAggregateRoot.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(supplier, 1L);

        when(coffeeproductionContextFacade.getSupplierById(1L)).thenReturn(Optional.of(supplier));
        when(coffeeproductionContextFacade.getAllSuppliers()).thenReturn(List.of(supplier));

        String json = """
                {
                  "name": "Finca Test",
                  "email": "test@test.com",
                  "phone": 987654321,
                  "location": "Jaen",
                  "specialties": []
                }
                """;

        mockMvc.perform(post("/api/v1/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Finca Test"))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    @DisplayName("GET /api/v1/suppliers retorna lista de proveedores (200)")
    void getAllSuppliersReturnsOk() throws Exception {
        when(currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of(7L));
        when(coffeeproductionContextFacade.getSuppliersByUserId(7L)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/suppliers"))
                .andExpect(status().isOk());
    }
}
