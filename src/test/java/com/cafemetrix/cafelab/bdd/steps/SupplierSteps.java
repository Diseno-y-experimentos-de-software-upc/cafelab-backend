package com.cafemetrix.cafelab.bdd.steps;

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

public class SupplierSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SharedSteps shared;

    @Given("un barista autenticado con perfil id {int} y proveedor listo para crear")
    public void unBaristaAutenticadoConPerfilIdYProveedorListoParaCrear(int profileId) {
        when(shared.currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of((long) profileId));
        var mockSupplier = mock(Supplier.class);
        when(mockSupplier.getId()).thenReturn(20L);
        when(mockSupplier.getUserId()).thenReturn((long) profileId);
        when(mockSupplier.getName()).thenReturn("Hacienda Colombia");
        when(mockSupplier.getEmail()).thenReturn("proveedor@cafelab.com");
        when(mockSupplier.getPhone()).thenReturn(1234567890L);
        when(mockSupplier.getLocation()).thenReturn("Huila Colombia");
        when(mockSupplier.getSpecialties()).thenReturn(List.of("Arabica"));
        when(shared.coffeeproductionContextFacade.createSupplier(anyLong(), anyString(), anyString(), anyLong(), anyString(), anyList()))
                .thenReturn(20L);
        when(shared.coffeeproductionContextFacade.getAllSuppliers()).thenReturn(List.of(mockSupplier));
    }

    @Given("un barista autenticado con perfil id {int} pero creación de proveedor falla")
    public void unBaristaAutenticadoPeroCreacionDeProveedorFalla(int profileId) {
        when(shared.currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of((long) profileId));
        when(shared.coffeeproductionContextFacade.createSupplier(anyLong(), anyString(), anyString(), anyLong(), anyString(), anyList()))
                .thenReturn(0L);
    }

    @When("envía una solicitud para crear un proveedor con nombre {string} email {string} telefono {long} ubicacion {string}")
    public void enviaSolicitudCrearProveedor(String nombre, String email, long telefono, String ubicacion) throws Exception {
        var body = Map.of(
                "name", nombre,
                "email", email,
                "phone", telefono,
                "location", ubicacion,
                "specialties", List.of("Arabica")
        );
        shared.lastResult = mockMvc.perform(post("/api/v1/suppliers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    @And("la respuesta contiene el id del proveedor creado")
    public void laRespuestaContieneElIdDelProveedorCreado() throws Exception {
        shared.lastResult.andExpect(jsonPath("$.id").isNotEmpty());
    }
}
