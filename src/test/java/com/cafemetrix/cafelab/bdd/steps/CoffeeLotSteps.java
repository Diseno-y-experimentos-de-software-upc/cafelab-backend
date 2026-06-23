package com.cafemetrix.cafelab.bdd.steps;

import com.cafemetrix.cafelab.production.domain.model.aggregates.CoffeeLot;
import com.cafemetrix.cafelab.production.domain.model.aggregates.Supplier;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class CoffeeLotSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SharedSteps shared;

    private Long supplierId;

    @Given("un barista autenticado con perfil id {int} y proveedor id {int} disponible para lote")
    public void unBaristaAutenticadoConPerfilIdYProveedorIdDisponibleParaLote(int profileId, int supplierId) {
        this.supplierId = (long) supplierId;
        when(shared.currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of((long) profileId));
        var mockSupplier = mock(Supplier.class);
        when(mockSupplier.getUserId()).thenReturn((long) profileId);
        when(shared.coffeeproductionContextFacade.getSupplierById(this.supplierId)).thenReturn(Optional.of(mockSupplier));
        when(shared.coffeeproductionContextFacade.createCoffeeLot(anyLong(), anyLong(), anyString(), anyString(), anyString(), anyInt(), anyDouble(), anyString(), anyString(), anyList()))
                .thenReturn(30L);
        var mockLot = mock(CoffeeLot.class);
        when(mockLot.getId()).thenReturn(30L);
        when(mockLot.getUserId()).thenReturn((long) profileId);
        when(mockLot.getSupplierId()).thenReturn(this.supplierId);
        when(shared.coffeeproductionContextFacade.getAllCoffeeLots()).thenReturn(List.of(mockLot));
    }

    @Given("un barista autenticado con perfil id {int} pero proveedor id {int} no le pertenece")
    public void unBaristaAutenticadoPeroProveedorIdNoLePertenece(int profileId, int supplierId) {
        this.supplierId = (long) supplierId;
        when(shared.currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of((long) profileId));
        when(shared.coffeeproductionContextFacade.getSupplierById(this.supplierId)).thenReturn(Optional.empty());
    }

    @When("envía una solicitud para crear un lote con nombre {string} tipo {string} metodo {string} altitud {int} peso {double} origen {string} estado {string}")
    public void enviaSolicitudCrearLote(String nombre, String tipo, String metodo, int altitud, double peso, String origen, String estado) throws Exception {
        var body = Map.of(
                "supplier_id", supplierId != null ? supplierId : 99L,
                "lot_name", nombre,
                "coffee_type", tipo,
                "processing_method", metodo,
                "altitude", altitud,
                "weight", peso,
                "origin", origen,
                "status", estado,
                "certifications", List.of()
        );
        shared.lastResult = mockMvc.perform(post("/api/v1/coffee-lots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    @And("la respuesta contiene el id del lote creado")
    public void laRespuestaContieneElIdDelLoteCreado() throws Exception {
        shared.lastResult.andExpect(jsonPath("$.id").isNotEmpty());
    }
}
