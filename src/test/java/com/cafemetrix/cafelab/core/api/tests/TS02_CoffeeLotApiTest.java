package com.cafemetrix.cafelab.core.api.tests;

import com.cafemetrix.cafelab.iam.infrastructure.authorization.sfs.support.CurrentProfileIdResolver;
import com.cafemetrix.cafelab.production.domain.model.aggregates.CoffeeLot;
import com.cafemetrix.cafelab.production.domain.model.aggregates.Supplier;
import com.cafemetrix.cafelab.production.domain.model.commands.CreateCoffeeLotCommand;
import com.cafemetrix.cafelab.production.domain.model.commands.CreateSupplierCommand;
import com.cafemetrix.cafelab.production.interfaces.acl.CoffeeproductionContextFacade;
import com.cafemetrix.cafelab.production.interfaces.rest.CoffeeLotsController;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CoffeeLotsController.class, 
            excludeAutoConfiguration = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
@DisplayName("TS02 - API Lotes")
class TS02_CoffeeLotApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CoffeeproductionContextFacade coffeeproductionContextFacade;

    @MockitoBean
    private CurrentProfileIdResolver currentProfileIdResolver;

    @Test
    @DisplayName("POST /api/v1/coffee-lots crea un lote exitosamente (201)")
    void createCoffeeLotReturnsCreated() throws Exception {
        when(currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of(7L));
        
        var supplier = new Supplier(new CreateSupplierCommand(
                7L, "Finca Test", "test@test.com", 987654321L, "Jaen", List.of()
        ));
        when(coffeeproductionContextFacade.getSupplierById(1L)).thenReturn(Optional.of(supplier));
        
        when(coffeeproductionContextFacade.createCoffeeLot(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(10L);
        
        var lot = new CoffeeLot(new CreateCoffeeLotCommand(
                7L, 1L, "Lote 1", "Arábica", "Lavado", 1500, 50.0, "Jaen", "green", List.of()
        ));
        
        // Asignar ID manualmente mediante reflexión para evitar el NPE en el controlador
        java.lang.reflect.Field idField = com.cafemetrix.cafelab.shared.domain.model.aggregates.AuditableAbstractAggregateRoot.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(lot, 10L);

        when(coffeeproductionContextFacade.getCoffeeLotById(10L)).thenReturn(Optional.of(lot));
        when(coffeeproductionContextFacade.getAllCoffeeLots()).thenReturn(List.of(lot));

        String json = """
                {
                  "supplier_id": 1,
                  "lot_name": "Lote 1",
                  "coffee_type": "Arábica",
                  "processing_method": "Lavado",
                  "altitude": 1500,
                  "weight": 50.0,
                  "origin": "Jaen",
                  "status": "green",
                  "certifications": []
                }
                """;

        mockMvc.perform(post("/api/v1/coffee-lots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.lotName").value("Lote 1"));
    }

    @Test
    @DisplayName("GET /api/v1/coffee-lots retorna lista de lotes (200)")
    void getAllCoffeeLotsReturnsOk() throws Exception {
        when(currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of(7L));
        when(coffeeproductionContextFacade.getCoffeeLotsByUserId(7L)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/coffee-lots"))
                .andExpect(status().isOk());
    }
}
