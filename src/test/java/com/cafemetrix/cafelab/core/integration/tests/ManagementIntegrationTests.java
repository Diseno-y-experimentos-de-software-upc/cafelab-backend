package com.cafemetrix.cafelab.core.integration.tests;


// US10: Control de Inventario Integrado

import com.cafemetrix.cafelab.iam.infrastructure.authorization.sfs.support.CurrentProfileIdResolver;
import com.cafemetrix.cafelab.management.domain.model.aggregates.InventoryEntry;
import com.cafemetrix.cafelab.management.interfaces.acl.ManagementContextFacade;
import com.cafemetrix.cafelab.management.interfaces.rest.InventoryEntriesController;
import com.cafemetrix.cafelab.management.interfaces.rest.resources.CreateInventoryEntryResource;
import com.cafemetrix.cafelab.production.domain.model.aggregates.CoffeeLot;
import com.cafemetrix.cafelab.production.interfaces.acl.CoffeeproductionContextFacade;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Optional;

public class ManagementIntegrationTests {
    private InventoryEntriesController inventoryEntriesController;

    // Facades
    private ManagementContextFacade managementContextFacade;
    private CoffeeproductionContextFacade coffeeproductionContextFacade;
    private CurrentProfileIdResolver currentProfileIdResolver;

    @BeforeEach
    void setUp() {
        // Facades
        managementContextFacade = Mockito.mock(ManagementContextFacade.class);
        coffeeproductionContextFacade = Mockito.mock(CoffeeproductionContextFacade.class);
        currentProfileIdResolver = Mockito.mock(CurrentProfileIdResolver.class);

        // Inventario
        inventoryEntriesController = new InventoryEntriesController(managementContextFacade, coffeeproductionContextFacade, currentProfileIdResolver);
    }

    // US10: Control de Inventario Integrado
    @Test
    void createInventoryEntry_ReturnsCreated_WhenValidInput() {
        var dateEntryCreated = LocalDateTime.now();

        var resource = new CreateInventoryEntryResource(
                1L,
                5.0,
                dateEntryCreated,
                "Café Molido",
                "retail",
                "Consumo de prueba"
        );

        InventoryEntry mockInventoryEntry = Mockito.mock(InventoryEntry.class);

        Mockito.when(mockInventoryEntry.getId()).thenReturn(1L);
        Mockito.when(mockInventoryEntry.getUserId()).thenReturn(1L);
        Mockito.when(mockInventoryEntry.getCoffeeLotId()).thenReturn(1L);
        Mockito.when(mockInventoryEntry.getQuantityUsed()).thenReturn(5.0);
        Mockito.when(mockInventoryEntry.getDateUsed()).thenReturn(dateEntryCreated);
        Mockito.when(mockInventoryEntry.getFinalProduct()).thenReturn("Café Molido");

        CoffeeLot mockCoffeeLot = Mockito.mock(CoffeeLot.class);
        Mockito.when(mockCoffeeLot.getId()).thenReturn(1L);
        Mockito.when(mockCoffeeLot.getUserId()).thenReturn(1L);

        Mockito.when(currentProfileIdResolver.resolveProfileId())
                .thenReturn(Optional.of(1L));

        Mockito.when(coffeeproductionContextFacade.getCoffeeLotById(1L))
                .thenReturn(Optional.of(mockCoffeeLot));

        Mockito.when(managementContextFacade.createInventoryEntry(Mockito.any()))
                .thenReturn(1L);

        Mockito.when(managementContextFacade.getInventoryEntryById(1L))
                .thenReturn(Optional.of(mockInventoryEntry));

        var response = inventoryEntriesController.createInventoryEntry(resource);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());

    }
}
