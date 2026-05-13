package com.cafemetrix.cafelab.bdd.steps;

import com.cafemetrix.cafelab.calibrations.domain.services.GrindCalibrationCommandService;
import com.cafemetrix.cafelab.calibrations.domain.services.GrindCalibrationQueryService;
import com.cafemetrix.cafelab.cuppingsessions.domain.services.CuppingSessionCommandService;
import com.cafemetrix.cafelab.cuppingsessions.domain.services.CuppingSessionQueryService;
import com.cafemetrix.cafelab.defects.domain.services.DefectCommandService;
import com.cafemetrix.cafelab.defects.domain.services.DefectQueryService;
import com.cafemetrix.cafelab.iam.domain.services.UserCommandService;
import com.cafemetrix.cafelab.iam.infrastructure.authorization.sfs.support.CurrentProfileIdResolver;
import com.cafemetrix.cafelab.management.interfaces.acl.ManagementContextFacade;
import com.cafemetrix.cafelab.preparation.interfaces.acl.PreparationContextFacade;
import com.cafemetrix.cafelab.production.interfaces.acl.CoffeeproductionContextFacade;
import com.cafemetrix.cafelab.profiles.domain.services.ProfileCommandService;
import com.cafemetrix.cafelab.profiles.domain.services.ProfileQueryService;
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

    @Autowired
    public DefectCommandService defectCommandService;

    @Autowired
    public DefectQueryService defectQueryService;

    @Autowired
    public PreparationContextFacade preparationContextFacade;

    @Autowired
    public GrindCalibrationCommandService grindCalibrationCommandService;

    @Autowired
    public GrindCalibrationQueryService grindCalibrationQueryService;

    @Autowired
    public ManagementContextFacade managementContextFacade;

    @Autowired
    public ProfileCommandService profileCommandService;

    @Autowired
    public ProfileQueryService profileQueryService;

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
