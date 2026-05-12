package com.cafemetrix.cafelab.core.unit.tests;

import com.cafemetrix.cafelab.management.application.acl.ManagementContextFacadeImpl;
import com.cafemetrix.cafelab.management.domain.exceptions.InsufficientCoffeeLotStockException;
import com.cafemetrix.cafelab.management.domain.model.aggregates.InventoryEntry;
import com.cafemetrix.cafelab.management.domain.model.commands.CreateInventoryEntryCommand;
import com.cafemetrix.cafelab.management.domain.services.InventoryEntryCommandService;
import com.cafemetrix.cafelab.management.domain.services.InventoryEntryQueryService;
import com.cafemetrix.cafelab.management.domain.services.ProductionCostRecordCommandService;
import com.cafemetrix.cafelab.management.domain.services.ProductionCostRecordQueryService;
import com.cafemetrix.cafelab.production.domain.model.aggregates.CoffeeLot;
import com.cafemetrix.cafelab.production.domain.model.commands.CreateCoffeeLotCommand;
import com.cafemetrix.cafelab.production.interfaces.acl.CoffeeproductionContextFacade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("US10 - Control de Inventario Integrado")
class US10_IntegratedInventoryTest {

    @Test
    @DisplayName("descuenta stock del lote al registrar inventario tostado")
    void deductsCoffeeLotStockWhenInventoryEntryIsCreated() {
        var inventoryCommandService = mock(InventoryEntryCommandService.class);
        var inventoryQueryService = mock(InventoryEntryQueryService.class);
        var costCommandService = mock(ProductionCostRecordCommandService.class);
        var costQueryService = mock(ProductionCostRecordQueryService.class);
        var productionFacade = mock(CoffeeproductionContextFacade.class);
        var facade = new ManagementContextFacadeImpl(
                inventoryCommandService, inventoryQueryService,
                costCommandService, costQueryService, productionFacade);
        
        // Fix: Usar CoffeeType válido ("Arábica")
        var lot = org.mockito.Mockito.spy(new CoffeeLot(new CreateCoffeeLotCommand(
                7L, 3L, "Lote Norte", "Arábica", "Lavado",
                1850, 100.0, "Jaen", "green", List.of("Organic"))));
        doReturn(44L).when(lot).getId();
        
        var entry = org.mockito.Mockito.spy(new InventoryEntry(new CreateInventoryEntryCommand(
                7L, 44L, 25.0, LocalDateTime.of(2026, 5, 12, 10, 0),
                "Cafe tostado")));
        doReturn(88L).when(entry).getId();
        
        when(productionFacade.getCoffeeLotById(44L)).thenReturn(Optional.of(lot));
        when(productionFacade.updateCoffeeLot(
                44L, "Lote Norte", "Arábica", "Lavado", 1850,
                75.0, "Jaen", "green", List.of("Organic"))).thenReturn(44L);
        when(inventoryCommandService.handle(any(CreateInventoryEntryCommand.class)))
                .thenReturn(Optional.of(entry));

        var createdId = facade.createInventoryEntry(new CreateInventoryEntryCommand(
                7L, 44L, 25.0, LocalDateTime.of(2026, 5, 12, 10, 0),
                "Cafe tostado"));

        assertThat(createdId).isEqualTo(88L);
        verify(productionFacade).updateCoffeeLot(
                44L, "Lote Norte", "Arábica", "Lavado", 1850,
                75.0, "Jaen", "green", List.of("Organic"));
    }

    @Test
    @DisplayName("bloquea salidas que exceden el stock disponible")
    void rejectsInventoryEntryOverAvailableStock() {
        var facade = new ManagementContextFacadeImpl(
                mock(InventoryEntryCommandService.class), mock(InventoryEntryQueryService.class),
                mock(ProductionCostRecordCommandService.class), mock(ProductionCostRecordQueryService.class),
                mock(CoffeeproductionContextFacade.class));
        var productionFacade = org.springframework.test.util.ReflectionTestUtils
                .getField(facade, "coffeeproductionContextFacade");
        var typedFacade = (CoffeeproductionContextFacade) productionFacade;
        
        // Fix: Usar CoffeeType válido ("Arábica")
        var lot = new CoffeeLot(new CreateCoffeeLotCommand(
                7L, 3L, "Lote Norte", "Arábica", "Lavado",
                1850, 10.0, "Jaen", "green", List.of()));
        when(typedFacade.getCoffeeLotById(44L)).thenReturn(Optional.of(lot));

        assertThatThrownBy(() -> facade.createInventoryEntry(new CreateInventoryEntryCommand(
                7L, 44L, 25.0, LocalDateTime.of(2026, 5, 12, 10, 0),
                "Cafe tostado")))
                .isInstanceOf(InsufficientCoffeeLotStockException.class);
    }
}
