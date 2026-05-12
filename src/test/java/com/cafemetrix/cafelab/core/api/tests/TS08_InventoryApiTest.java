package com.cafemetrix.cafelab.core.api.tests;

import com.cafemetrix.cafelab.iam.infrastructure.authorization.sfs.support.CurrentProfileIdResolver;
import com.cafemetrix.cafelab.management.domain.model.aggregates.InventoryEntry;
import com.cafemetrix.cafelab.management.domain.model.commands.CreateInventoryEntryCommand;
import com.cafemetrix.cafelab.management.interfaces.acl.ManagementContextFacade;
import com.cafemetrix.cafelab.management.interfaces.rest.InventoryEntriesController;
import com.cafemetrix.cafelab.production.domain.model.aggregates.CoffeeLot;
import com.cafemetrix.cafelab.production.domain.model.commands.CreateCoffeeLotCommand;
import com.cafemetrix.cafelab.production.interfaces.acl.CoffeeproductionContextFacade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = InventoryEntriesController.class, 
            excludeAutoConfiguration = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
@DisplayName("TS08 - API Inventario")
class TS08_InventoryApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ManagementContextFacade managementContextFacade;

    @MockitoBean
    private CoffeeproductionContextFacade coffeeproductionContextFacade;

    @MockitoBean
    private CurrentProfileIdResolver currentProfileIdResolver;

    @Test
    @DisplayName("POST /api/v1/inventory-entries registra consumo exitosamente (201)")
    void createInventoryEntryReturnsCreated() throws Exception {
        when(currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of(7L));
        
        var lot = new CoffeeLot(new CreateCoffeeLotCommand(
                7L, 1L, "Lote 1", "Arábica", "Lavado", 1500, 50.0, "Jaen", "green", List.of()
        ));
        when(coffeeproductionContextFacade.getCoffeeLotById(1L)).thenReturn(Optional.of(lot));
        
        when(managementContextFacade.createInventoryEntry(any())).thenReturn(50L);
        
        var entry = new InventoryEntry(new CreateInventoryEntryCommand(
                7L, 1L, 2.5, LocalDateTime.now(), "Latte"
        ));
        when(managementContextFacade.getInventoryEntryById(50L)).thenReturn(Optional.of(entry));

        String json = """
                {
                  "coffeeLotId": 1,
                  "quantityUsed": 2.5,
                  "dateUsed": "2026-05-12T10:00:00",
                  "finalProduct": "Latte"
                }
                """;

        mockMvc.perform(post("/api/v1/inventory-entries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.finalProduct").value("Latte"));
    }

    @Test
    @DisplayName("GET /api/v1/inventory-entries retorna lista de entradas (200)")
    void getAllInventoryEntriesReturnsOk() throws Exception {
        when(currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of(7L));
        when(managementContextFacade.getInventoryEntriesByUserId(7L)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/inventory-entries"))
                .andExpect(status().isOk());
    }
}
