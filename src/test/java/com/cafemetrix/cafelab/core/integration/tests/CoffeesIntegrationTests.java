package com.cafemetrix.cafelab.core.integration.tests;

import com.cafemetrix.cafelab.coffees.domain.model.aggregates.Coffee;
import com.cafemetrix.cafelab.coffees.domain.model.commands.CreateCoffeeCommand;
import com.cafemetrix.cafelab.coffees.domain.model.queries.GetAllCoffeesQuery;
import com.cafemetrix.cafelab.coffees.domain.model.queries.GetCoffeeByIdQuery;
import com.cafemetrix.cafelab.coffees.domain.services.CoffeeCommandService;
import com.cafemetrix.cafelab.coffees.domain.services.CoffeeQueryService;
import com.cafemetrix.cafelab.coffees.interfaces.rest.CoffeesController;
import com.cafemetrix.cafelab.coffees.interfaces.rest.resources.CreateCoffeeResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

// US03: Biblioteca de Defectos de Tueste

public class CoffeesIntegrationTests {
    private CoffeeCommandService coffeeCommandService;
    private CoffeeQueryService coffeeQueryService;

    private CoffeesController coffeesController;

    @BeforeEach
    void setUp() {
        coffeeCommandService = Mockito.mock(CoffeeCommandService.class);
        coffeeQueryService = Mockito.mock(CoffeeQueryService.class);

        coffeesController = new CoffeesController(coffeeCommandService, coffeeQueryService);
    }

    @Test
    void createCoffee_ReturnsCreated_WhenValidInput() {
        var resource = new CreateCoffeeResource(
                "Café Supremo",
                "Colombia",
                "Arabica",
                100.0
        );

        Coffee mockCoffee = Mockito.mock(Coffee.class);

        Mockito.when(mockCoffee.getId()).thenReturn(1L);
        Mockito.when(mockCoffee.getName()).thenReturn("Café Supremo");
        Mockito.when(mockCoffee.getRegion()).thenReturn("Colombia");
        Mockito.when(mockCoffee.getVariety()).thenReturn("Arabica");
        Mockito.when(mockCoffee.getTotalWeight()).thenReturn(100.0);

        Mockito.when(coffeeCommandService.handle(Mockito.any(CreateCoffeeCommand.class)))
                .thenReturn(Optional.of(mockCoffee));

        var response = coffeesController.createCoffee(resource);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    void getCoffeeById_ReturnsOk_WhenCoffeeExists() {
        Coffee mockCoffee = Mockito.mock(Coffee.class);

        Mockito.when(mockCoffee.getId()).thenReturn(1L);
        Mockito.when(mockCoffee.getName()).thenReturn("Café Supremo");
        Mockito.when(mockCoffee.getRegion()).thenReturn("Colombia");
        Mockito.when(mockCoffee.getVariety()).thenReturn("Arabica");
        Mockito.when(mockCoffee.getTotalWeight()).thenReturn(100.0);

        Mockito.when(coffeeQueryService.handle(Mockito.any(GetCoffeeByIdQuery.class)))
                .thenReturn(Optional.of(mockCoffee));

        var response = coffeesController.getCoffeeById(1L);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    void getAllCoffees_ReturnsOk_WhenCoffeesExist() {
        Coffee mockCoffee = Mockito.mock(Coffee.class);

        Mockito.when(mockCoffee.getId()).thenReturn(1L);
        Mockito.when(mockCoffee.getName()).thenReturn("Café Supremo");
        Mockito.when(mockCoffee.getRegion()).thenReturn("Colombia");
        Mockito.when(mockCoffee.getVariety()).thenReturn("Arabica");
        Mockito.when(mockCoffee.getTotalWeight()).thenReturn(100.0);

        Mockito.when(coffeeQueryService.handle(Mockito.any(GetAllCoffeesQuery.class)))
                .thenReturn(List.of(mockCoffee));

        var response = coffeesController.getAllCoffees();

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }
}
