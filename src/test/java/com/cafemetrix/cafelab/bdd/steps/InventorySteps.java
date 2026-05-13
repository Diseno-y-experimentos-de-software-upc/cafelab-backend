package com.cafemetrix.cafelab.bdd.steps;

import com.cafemetrix.cafelab.management.domain.model.aggregates.InventoryEntry;
import com.cafemetrix.cafelab.production.domain.model.aggregates.CoffeeLot;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class InventorySteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SharedSteps shared;

    @Given("un barista autenticado con perfil id {int} y lote id {int} disponible para inventario")
    public void unBaristaAutenticadoConPerfilIdYLoteIdDisponibleParaInventario(int profileId, int lotId) {
        when(shared.currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of((long) profileId));
        var mockLot = mock(CoffeeLot.class);
        when(mockLot.getUserId()).thenReturn((long) profileId);
        when(shared.coffeeproductionContextFacade.getCoffeeLotById((long) lotId)).thenReturn(Optional.of(mockLot));
        when(shared.managementContextFacade.createInventoryEntry(any())).thenReturn(80L);
        var mockEntry = mock(InventoryEntry.class);
        when(mockEntry.getId()).thenReturn(80L);
        when(mockEntry.getUserId()).thenReturn((long) profileId);
        when(shared.managementContextFacade.getInventoryEntryById(80L)).thenReturn(Optional.of(mockEntry));
    }

    @Given("un barista autenticado con perfil id {int} pero lote id {int} no le pertenece para inventario")
    public void unBaristaAutenticadoPeroLoteNoLePertenece(int profileId, int lotId) {
        when(shared.currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of((long) profileId));
        when(shared.coffeeproductionContextFacade.getCoffeeLotById((long) lotId)).thenReturn(Optional.empty());
    }

    @When("envía una solicitud para crear una entrada de inventario con lote {int} cantidad {double} producto {string}")
    public void enviaSolicitudCrearEntradaInventario(int lotId, double cantidad, String producto) throws Exception {
        var body = Map.of(
                "coffeeLotId", lotId,
                "quantityUsed", cantidad,
                "dateUsed", "2026-05-13T10:00:00",
                "finalProduct", producto
        );
        shared.lastResult = mockMvc.perform(post("/api/v1/inventory-entries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    @And("la respuesta contiene el id de la entrada de inventario creada")
    public void laRespuestaContieneElIdDeLaEntradaDeInventarioCreada() throws Exception {
        shared.lastResult.andExpect(jsonPath("$.id").isNotEmpty());
    }
}
