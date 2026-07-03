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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        when(shared.coffeeproductionContextFacade.createSupplier(anyLong(), anyString(), anyString(), anyLong(), anyString(), anyList(), any(), any()))
                .thenReturn(20L);
        when(shared.coffeeproductionContextFacade.getAllSuppliers()).thenReturn(List.of(mockSupplier));
    }

    @Given("un barista autenticado con perfil id {int} pero creación de proveedor falla")
    public void unBaristaAutenticadoPeroCreacionDeProveedorFalla(int profileId) {
        when(shared.currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of((long) profileId));
        when(shared.coffeeproductionContextFacade.createSupplier(anyLong(), anyString(), anyString(), anyLong(), anyString(), anyList(), any(), any()))
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

    // ===== US01b: Ficha de proveedor ampliada (contactPerson + webLink) =====

    @Given("un barista autenticado con perfil id {int} y proveedor ampliado listo para crear")
    public void unBaristaAutenticadoConProveedorAmpliadoListoParaCrear(int profileId) {
        when(shared.currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of((long) profileId));
        var mockSupplier = mock(Supplier.class);
        when(mockSupplier.getId()).thenReturn(20L);
        when(mockSupplier.getUserId()).thenReturn((long) profileId);
        when(mockSupplier.getName()).thenReturn("Hacienda Colombia");
        when(mockSupplier.getEmail()).thenReturn("proveedor@cafelab.com");
        when(mockSupplier.getPhone()).thenReturn(1234567890L);
        when(mockSupplier.getLocation()).thenReturn("Huila Colombia");
        when(mockSupplier.getSpecialties()).thenReturn(List.of("Arabica"));
        when(mockSupplier.getContactPerson()).thenReturn("Maria Lopez");
        when(mockSupplier.getWebLink()).thenReturn("https://hacienda.com");
        when(shared.coffeeproductionContextFacade.createSupplier(
                anyLong(), anyString(), anyString(), anyLong(), anyString(), anyList(), any(), any()))
                .thenReturn(20L);
        when(shared.coffeeproductionContextFacade.getAllSuppliers()).thenReturn(List.of(mockSupplier));
    }

    @Given("un barista autenticado con perfil id {int} y proveedor ampliado existente con id {long}")
    public void unBaristaAutenticadoConProveedorAmpliadoExistente(int profileId, long supplierId) {
        when(shared.currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of((long) profileId));
        var mockSupplier = mock(Supplier.class);
        when(mockSupplier.getId()).thenReturn(supplierId);
        when(mockSupplier.getUserId()).thenReturn((long) profileId);
        when(mockSupplier.getName()).thenReturn("Hacienda Colombia");
        when(mockSupplier.getEmail()).thenReturn("proveedor@cafelab.com");
        when(mockSupplier.getPhone()).thenReturn(1234567890L);
        when(mockSupplier.getLocation()).thenReturn("Huila Colombia");
        when(mockSupplier.getSpecialties()).thenReturn(List.of("Arabica"));
        when(mockSupplier.getContactPerson()).thenReturn("Maria Lopez");
        when(mockSupplier.getWebLink()).thenReturn("https://hacienda.com");
        when(shared.coffeeproductionContextFacade.getSupplierById(supplierId))
                .thenReturn(Optional.of(mockSupplier));
    }

    @When("envía una solicitud para crear un proveedor ampliado con contacto {string} y enlace web {string}")
    public void enviaSolicitudCrearProveedorAmpliado(String contacto, String enlace) throws Exception {
        var body = Map.of(
                "name", "Hacienda Colombia",
                "email", "proveedor@cafelab.com",
                "phone", 1234567890L,
                "location", "Huila Colombia",
                "specialties", List.of("Arabica"),
                "contactPerson", contacto,
                "webLink", enlace
        );
        shared.lastResult = mockMvc.perform(post("/api/v1/suppliers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));
    }

    @When("consulta el detalle del proveedor con id {long}")
    public void consultaElDetalleDelProveedor(long supplierId) throws Exception {
        shared.lastResult = mockMvc.perform(get("/api/v1/suppliers/" + supplierId)
                .contentType(MediaType.APPLICATION_JSON));
    }

    @And("la respuesta del proveedor contiene contacto {string}")
    public void laRespuestaContieneContacto(String contacto) throws Exception {
        shared.lastResult.andExpect(jsonPath("$.contactPerson").value(contacto));
    }

    @And("la respuesta del proveedor contiene enlace web {string}")
    public void laRespuestaContieneEnlaceWeb(String enlace) throws Exception {
        shared.lastResult.andExpect(jsonPath("$.webLink").value(enlace));
    }
}
