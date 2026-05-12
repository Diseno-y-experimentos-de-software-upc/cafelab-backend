package com.cafemetrix.cafelab.core.api.tests;

import com.cafemetrix.cafelab.iam.infrastructure.authorization.sfs.support.CurrentProfileIdResolver;
import com.cafemetrix.cafelab.production.domain.model.aggregates.CoffeeLot;
import com.cafemetrix.cafelab.production.domain.model.aggregates.RoastProfile;
import com.cafemetrix.cafelab.production.domain.model.commands.CreateCoffeeLotCommand;
import com.cafemetrix.cafelab.production.domain.model.commands.CreateRoastProfileCommand;
import com.cafemetrix.cafelab.production.interfaces.acl.CoffeeproductionContextFacade;
import com.cafemetrix.cafelab.production.interfaces.rest.RoastProfilesController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RoastProfilesController.class, 
            excludeAutoConfiguration = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class})
@DisplayName("TS03 - API Perfiles de Tueste")
class TS03_RoastProfileApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CoffeeproductionContextFacade coffeeproductionContextFacade;

    @MockitoBean
    private CurrentProfileIdResolver currentProfileIdResolver;

    @Test
    @DisplayName("POST /api/v1/roast-profile crea un perfil exitosamente (201)")
    void createRoastProfileReturnsCreated() throws Exception {
        when(currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of(7L));
        
        var lot = new CoffeeLot(new CreateCoffeeLotCommand(
                7L, 1L, "Lote 1", "Arábica", "Lavado", 1500, 50.0, "Jaen", "green", List.of()
        ));
        when(coffeeproductionContextFacade.getCoffeeLotById(1L)).thenReturn(Optional.of(lot));
        
        when(coffeeproductionContextFacade.createRoastProfile(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(20L);
        
        var profile = new RoastProfile(new CreateRoastProfileCommand(
                7L, "Perfil Test", "Ligero", 10, 160.0, 205.0, 1L, false
        ));
        when(coffeeproductionContextFacade.getRoastProfileById(20L)).thenReturn(Optional.of(profile));

        String json = """
                {
                  "name": "Perfil Test",
                  "type": "Ligero",
                  "duration": 10,
                  "tempStart": 160.0,
                  "tempEnd": 205.0,
                  "lot": 1,
                  "isFavorite": false
                }
                """;

        mockMvc.perform(post("/api/v1/roast-profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Perfil Test"));
    }

    @Test
    @DisplayName("GET /api/v1/roast-profile retorna lista de perfiles (200)")
    void getAllRoastProfilesReturnsOk() throws Exception {
        when(currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of(7L));
        when(coffeeproductionContextFacade.getRoastProfilesByUserId(7L)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/roast-profile"))
                .andExpect(status().isOk());
    }
}
