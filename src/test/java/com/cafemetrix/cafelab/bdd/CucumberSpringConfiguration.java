package com.cafemetrix.cafelab.bdd;

import com.cafemetrix.cafelab.calibrations.domain.services.GrindCalibrationCommandService;
import com.cafemetrix.cafelab.calibrations.domain.services.GrindCalibrationQueryService;
import com.cafemetrix.cafelab.calibrations.interfaces.rest.CalibrationsController;
import com.cafemetrix.cafelab.calibrations.interfaces.rest.CalibrationsExceptionHandler;
import com.cafemetrix.cafelab.cuppingsessions.domain.services.CuppingSessionCommandService;
import com.cafemetrix.cafelab.cuppingsessions.domain.services.CuppingSessionQueryService;
import com.cafemetrix.cafelab.cuppingsessions.interfaces.rest.CuppingSessionsController;
import com.cafemetrix.cafelab.cuppingsessions.interfaces.rest.CuppingSessionsExceptionHandler;
import com.cafemetrix.cafelab.defects.domain.services.DefectCommandService;
import com.cafemetrix.cafelab.defects.domain.services.DefectQueryService;
import com.cafemetrix.cafelab.defects.interfaces.rest.DefectsController;
import com.cafemetrix.cafelab.defects.interfaces.rest.DefectsExceptionHandler;
import com.cafemetrix.cafelab.iam.domain.services.UserCommandService;
import com.cafemetrix.cafelab.iam.infrastructure.authorization.sfs.support.CurrentProfileIdResolver;
import com.cafemetrix.cafelab.iam.interfaces.rest.AuthenticationController;
import com.cafemetrix.cafelab.iam.interfaces.rest.IamExceptionHandler;
import com.cafemetrix.cafelab.management.interfaces.acl.ManagementContextFacade;
import com.cafemetrix.cafelab.management.interfaces.rest.InventoryEntriesController;
import com.cafemetrix.cafelab.management.interfaces.rest.ManagementExceptionHandler;
import com.cafemetrix.cafelab.management.interfaces.rest.ProductionCostRecordsController;
import com.cafemetrix.cafelab.preparation.interfaces.acl.PreparationContextFacade;
import com.cafemetrix.cafelab.preparation.interfaces.rest.PortfoliosController;
import com.cafemetrix.cafelab.preparation.interfaces.rest.PreparationExceptionHandler;
import com.cafemetrix.cafelab.preparation.interfaces.rest.RecipesController;
import com.cafemetrix.cafelab.production.interfaces.acl.CoffeeproductionContextFacade;
import com.cafemetrix.cafelab.production.interfaces.rest.CoffeeLotsController;
import com.cafemetrix.cafelab.production.interfaces.rest.ProductionExceptionHandler;
import com.cafemetrix.cafelab.production.interfaces.rest.RoastProfilesController;
import com.cafemetrix.cafelab.production.interfaces.rest.SuppliersController;
import com.cafemetrix.cafelab.profiles.domain.services.ProfileCommandService;
import com.cafemetrix.cafelab.profiles.domain.services.ProfileQueryService;
import com.cafemetrix.cafelab.profiles.interfaces.rest.ProfilesController;
import com.cafemetrix.cafelab.profiles.interfaces.rest.ProfilesExceptionHandler;
import com.cafemetrix.cafelab.shared.interfaces.rest.GlobalExceptionHandler;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

@CucumberContextConfiguration
@WebMvcTest(
    controllers = {
        AuthenticationController.class,
        RoastProfilesController.class,
        CuppingSessionsController.class,
        SuppliersController.class,
        CoffeeLotsController.class,
        DefectsController.class,
        RecipesController.class,
        CalibrationsController.class,
        PortfoliosController.class,
        InventoryEntriesController.class,
        ProductionCostRecordsController.class,
        ProfilesController.class
    },
    excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
@Import({
    GlobalExceptionHandler.class,
    IamExceptionHandler.class,
    ProductionExceptionHandler.class,
    CuppingSessionsExceptionHandler.class,
    CalibrationsExceptionHandler.class,
    DefectsExceptionHandler.class,
    ManagementExceptionHandler.class,
    PreparationExceptionHandler.class,
    ProfilesExceptionHandler.class
})
public class CucumberSpringConfiguration {

    @MockBean
    public UserCommandService userCommandService;

    @MockBean
    public CoffeeproductionContextFacade coffeeproductionContextFacade;

    @MockBean
    public CurrentProfileIdResolver currentProfileIdResolver;

    @MockBean
    public CuppingSessionCommandService cuppingSessionCommandService;

    @MockBean
    public CuppingSessionQueryService cuppingSessionQueryService;

    @MockBean
    public DefectCommandService defectCommandService;

    @MockBean
    public DefectQueryService defectQueryService;

    @MockBean
    public PreparationContextFacade preparationContextFacade;

    @MockBean
    public GrindCalibrationCommandService grindCalibrationCommandService;

    @MockBean
    public GrindCalibrationQueryService grindCalibrationQueryService;

    @MockBean
    public ManagementContextFacade managementContextFacade;

    @MockBean
    public ProfileCommandService profileCommandService;

    @MockBean
    public ProfileQueryService profileQueryService;
}
