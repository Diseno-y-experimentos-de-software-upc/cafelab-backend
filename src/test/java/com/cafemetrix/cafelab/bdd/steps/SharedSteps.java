package com.cafemetrix.cafelab.bdd.steps;

import com.cafemetrix.cafelab.cuppingsessions.domain.services.CuppingSessionCommandService;
import com.cafemetrix.cafelab.cuppingsessions.domain.services.CuppingSessionQueryService;
import com.cafemetrix.cafelab.iam.domain.services.UserCommandService;
import com.cafemetrix.cafelab.iam.infrastructure.authorization.sfs.support.CurrentProfileIdResolver;
import com.cafemetrix.cafelab.production.interfaces.acl.CoffeeproductionContextFacade;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SharedSteps {

    @Autowired
    public UserCommandService userCommandService;

    @Autowired
    public CoffeeproductionContextFacade coffeeproductionContextFacade;

    @Autowired
    public CurrentProfileIdResolver currentProfileIdResolver;

    @Autowired
    public CuppingSessionCommandService cuppingSessionCommandService;

    @Autowired
    public CuppingSessionQueryService cuppingSessionQueryService;

    ResultActions lastResult;

    @Given("un usuario no autenticado")
    public void unUsuarioNoAutenticado() {
        when(currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.empty());
    }

    @Then("el sistema responde con código {int}")
    public void elSistemaRespondeConCodigo(int statusCode) throws Exception {
        lastResult.andExpect(status().is(statusCode));
    }
}
