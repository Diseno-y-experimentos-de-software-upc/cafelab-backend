package com.cafemetrix.cafelab.core.integration.tests;

import com.cafemetrix.cafelab.iam.infrastructure.authorization.sfs.support.CurrentProfileIdResolver;
import com.cafemetrix.cafelab.production.domain.model.aggregates.CoffeeLot;
import com.cafemetrix.cafelab.production.domain.model.aggregates.RoastProfile;
import com.cafemetrix.cafelab.production.domain.model.aggregates.Supplier;
import com.cafemetrix.cafelab.production.domain.model.valueobjects.CoffeeLotName;
import com.cafemetrix.cafelab.production.interfaces.acl.CoffeeproductionContextFacade;
import com.cafemetrix.cafelab.production.interfaces.rest.CoffeeLotsController;
import com.cafemetrix.cafelab.production.interfaces.rest.RoastProfilesController;
import com.cafemetrix.cafelab.production.interfaces.rest.SuppliersController;
import com.cafemetrix.cafelab.production.interfaces.rest.resources.CoffeeLotResource;
import com.cafemetrix.cafelab.production.interfaces.rest.resources.CreateCoffeeLotResource;
import com.cafemetrix.cafelab.production.interfaces.rest.resources.CreateRoastProfileResource;
import com.cafemetrix.cafelab.production.interfaces.rest.resources.CreateSupplierResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

// US01: Registro de Proovedores
// US02: Gestión de Lotes de Café Verde
// US03: Creación de Perfil de Tueste

public class ProductionIntegrationTests {
    // Proovedores - Suppliers
    private SuppliersController suppliersController;

    // Lotes de Café Verde - Coffee Lots
    private CoffeeLotsController coffeeLotsController;

    // Perfil de Tueste - Roast Profiles
    private RoastProfilesController roastProfilesController;

    // Facades
    private CoffeeproductionContextFacade coffeeproductionContextFacade;
    private CurrentProfileIdResolver currentProfileIdResolver;

    @BeforeEach
    void setUp() {
        // Facades
        coffeeproductionContextFacade = Mockito.mock(CoffeeproductionContextFacade.class);
        currentProfileIdResolver = Mockito.mock(CurrentProfileIdResolver.class);

        // Proovedores
        suppliersController = new SuppliersController(coffeeproductionContextFacade, currentProfileIdResolver);

        // Lotes de Café Verde
        coffeeLotsController = new CoffeeLotsController(coffeeproductionContextFacade, currentProfileIdResolver);

        // Perfil de Tueste
        roastProfilesController = new RoastProfilesController(coffeeproductionContextFacade, currentProfileIdResolver);
    }

    // US01: Registro de Proovedores
    @Test
    void createSupplier_ReturnsCreated_WhenValidInput() {
        var resource = new CreateSupplierResource(
                "Arturo Diaz",
                "test@test.com",
                987456321L,
                "Av. La Molina",
                List.of("Arabica", "Robusta")
        );

        Supplier mockSupplier = Mockito.mock(Supplier.class);

        Mockito.when(mockSupplier.getId()).thenReturn(1L);
        Mockito.when(mockSupplier.getUserId()).thenReturn(1L);
        Mockito.when(mockSupplier.getName()).thenReturn("Arturo Diaz");
        Mockito.when(mockSupplier.getEmail()).thenReturn("test@test.com");
        Mockito.when(mockSupplier.getPhone()).thenReturn(987456321L);
        Mockito.when(mockSupplier.getLocation()).thenReturn("Av. La Molina");
        Mockito.when(mockSupplier.getSpecialties()).thenReturn(List.of("Arabica", "Robusta"));

        Mockito.when(currentProfileIdResolver.resolveProfileId())
                        .thenReturn(Optional.of(1L));

        Mockito.when(coffeeproductionContextFacade.getAllSuppliers())
                        .thenReturn(List.of(mockSupplier));

        Mockito.when(coffeeproductionContextFacade.createSupplier(
                Mockito.anyLong(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyLong(),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(1L);

        var response = suppliersController.createSupplier(resource);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }

    // US02: Gestión de Lotes de Café Verde
    @Test
    void createCoffeeLot_RetursCreated_WhenValidInput() {
        var resource = new CreateCoffeeLotResource(
                1L,
                "Lote 1",
                "Arabica",
                "Washing",
                1500,
                100.5,
                "Africa",
                "Available",
                List.of("Organic", "Fair Trade")
        );

        CoffeeLot mockCoffeeLot = Mockito.mock(CoffeeLot.class);

        Mockito.when(mockCoffeeLot.getId()).thenReturn(1L);
        Mockito.when(mockCoffeeLot.getUserId()).thenReturn(1L);
        Mockito.when(mockCoffeeLot.getSupplierId()).thenReturn(1L);
        Mockito.when(mockCoffeeLot.getLotName()).thenReturn(new CoffeeLotName("Lote 1").value());
        Mockito.when(mockCoffeeLot.getCoffeeType()).thenReturn("Arabica");
        Mockito.when(mockCoffeeLot.getProcessingMethod()).thenReturn("Washing");
        Mockito.when(mockCoffeeLot.getAltitude()).thenReturn(1500);
        Mockito.when(mockCoffeeLot.getWeight()).thenReturn(100.5);
        Mockito.when(mockCoffeeLot.getOrigin()).thenReturn("Africa");
        Mockito.when(mockCoffeeLot.getStatus()).thenReturn("Available");
        Mockito.when(mockCoffeeLot.getCertifications()).thenReturn(List.of("Organic", "Fair Trade"));

        Mockito.when(currentProfileIdResolver.resolveProfileId())
                .thenReturn(Optional.of(1L));

        Supplier mockSupplier = Mockito.mock(Supplier.class);

        Mockito.when(mockSupplier.getId()).thenReturn(1L);
        Mockito.when(mockSupplier.getUserId()).thenReturn(1L);

        Mockito.when(coffeeproductionContextFacade.getSupplierById(1L))
                .thenReturn(Optional.of(mockSupplier));

        Mockito.when(coffeeproductionContextFacade.createCoffeeLot(
                Mockito.anyLong(),
                Mockito.anyLong(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyDouble(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyList()
        )).thenReturn(1L);

        Mockito.when(coffeeproductionContextFacade.getAllCoffeeLots())
                .thenReturn(List.of(mockCoffeeLot));

        var response = coffeeLotsController.createCoffeeLot(resource);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());

    }

    // US03: Creación de Perfil de Tueste
    @Test
    void createRoastProfile_ReturnsCreated_WhenValidInput() {
        var resource = new CreateRoastProfileResource(
                "Perfil 1",
                "Medium",
                15,
                200.0,
                220.0,
                1L,
                false
        );

        RoastProfile mockRoastProfile = Mockito.mock(RoastProfile.class);

        Mockito.when(mockRoastProfile.getId()).thenReturn(1L);
        Mockito.when(mockRoastProfile.getUserId()).thenReturn(1L);
        Mockito.when(mockRoastProfile.getName()).thenReturn("Perfil 1");
        Mockito.when(mockRoastProfile.getType()).thenReturn("Medium");
        Mockito.when(mockRoastProfile.getDuration()).thenReturn(15);
        Mockito.when(mockRoastProfile.getTempStart()).thenReturn(200.0);
        Mockito.when(mockRoastProfile.getTempEnd()).thenReturn(220.0);
        Mockito.when(mockRoastProfile.getCoffeeLotId()).thenReturn(1L);
        Mockito.when(mockRoastProfile.getIsFavorite()).thenReturn(false);

        Mockito.when(currentProfileIdResolver.resolveProfileId())
                .thenReturn(Optional.of(1L));

        CoffeeLot mockCoffeeLot = Mockito.mock(CoffeeLot.class);

        Mockito.when(mockCoffeeLot.getId()).thenReturn(1L);
        Mockito.when(mockCoffeeLot.getUserId()).thenReturn(1L);
        Mockito.when(mockCoffeeLot.getSupplierId()).thenReturn(1L);

        Mockito.when(coffeeproductionContextFacade.getCoffeeLotById(1L))
                .thenReturn(Optional.of(mockCoffeeLot));

        Mockito.when(coffeeproductionContextFacade.createRoastProfile(
                Mockito.anyLong(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyInt(),
                Mockito.anyDouble(),
                Mockito.anyDouble(),
                Mockito.anyLong(),
                Mockito.anyBoolean()
        )).thenReturn(1L);

        Mockito.when(coffeeproductionContextFacade.getRoastProfileById(1L))
                .thenReturn(Optional.of(mockRoastProfile));

        Mockito.when(coffeeproductionContextFacade.getAllRoastProfiles())
                .thenReturn(List.of(mockRoastProfile));

        var response = roastProfilesController.createRoastProfile(resource);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());

    }
}