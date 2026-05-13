package com.cafemetrix.cafelab.bdd.steps;

import com.cafemetrix.cafelab.management.domain.model.aggregates.ProductionCostRecord;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class ProductionCostSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SharedSteps shared;

    @Given("un barista autenticado con perfil id {int} y lote id {int} disponible para costos")
    public void unBaristaAutenticadoConPerfilIdYLoteIdDisponibleParaCostos(int profileId, int lotId) {
        when(shared.currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of((long) profileId));
        var mockLot = mock(CoffeeLot.class);
        when(mockLot.getId()).thenReturn((long) lotId);
        when(mockLot.getUserId()).thenReturn((long) profileId);
        when(mockLot.getLotName()).thenReturn("Lote Test");
        when(mockLot.getCoffeeType()).thenReturn("Arabica");
        when(shared.coffeeproductionContextFacade.getCoffeeLotById((long) lotId)).thenReturn(Optional.of(mockLot));
        when(shared.managementContextFacade.createProductionCostRecord(any())).thenReturn(90L);
        var mockRecord = mock(ProductionCostRecord.class);
        when(mockRecord.getId()).thenReturn(90L);
        when(mockRecord.getUserId()).thenReturn((long) profileId);
        when(shared.managementContextFacade.getProductionCostRecordByIdAndUserId(eq(90L), eq((long) profileId)))
                .thenReturn(Optional.of(mockRecord));
    }

    @Given("un barista autenticado con perfil id {int} pero lote id {int} no le pertenece para costos")
    public void unBaristaAutenticadoPeroLoteNoLePerteneceCostos(int profileId, int lotId) {
        when(shared.currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of((long) profileId));
        when(shared.coffeeproductionContextFacade.getCoffeeLotById((long) lotId)).thenReturn(Optional.empty());
    }

    @When("envía una solicitud para crear un registro de costo con lote {int} moneda {string} kg {double} materia {double} mano {double} transporte {double} almacenamiento {double} procesamiento {double} otros {double}")
    public void enviaSolicitudCrearRegistroCosto(int lotId, String moneda, double kg, double materia, double mano, double transporte, double almacenamiento, double procesamiento, double otros) throws Exception {
        var body = Map.of(
                "coffeeLotId", lotId,
                "currency", moneda,
                "totalKg", kg,
                "rawMaterialsCost", materia,
                "laborCost", mano,
                "transportCost", transporte,
                "storageCost", almacenamiento,
                "processingCost", procesamiento,
                "otherIndirectCosts", otros
        );
        shared.lastResult = mockMvc.perform(post("/api/v1/production-cost-records")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    @And("la respuesta contiene el id del registro de costo creado")
    public void laRespuestaContieneElIdDelRegistroDeCostoCreado() throws Exception {
        shared.lastResult.andExpect(jsonPath("$.id").isNotEmpty());
    }
}
