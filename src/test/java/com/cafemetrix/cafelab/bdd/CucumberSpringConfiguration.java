package com.cafemetrix.cafelab.bdd;

import com.cafemetrix.cafelab.cuppingsessions.interfaces.rest.CuppingSessionsController;
import com.cafemetrix.cafelab.cuppingsessions.interfaces.rest.CuppingSessionsExceptionHandler;
import com.cafemetrix.cafelab.iam.interfaces.rest.AuthenticationController;
import com.cafemetrix.cafelab.iam.interfaces.rest.IamExceptionHandler;
import com.cafemetrix.cafelab.production.interfaces.rest.ProductionExceptionHandler;
import com.cafemetrix.cafelab.production.interfaces.rest.RoastProfilesController;
import com.cafemetrix.cafelab.shared.interfaces.rest.GlobalExceptionHandler;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;

@CucumberContextConfiguration
@WebMvcTest(
    controllers = {
        AuthenticationController.class,
        RoastProfilesController.class,
        CuppingSessionsController.class
    },
    excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
@Import({GlobalExceptionHandler.class, IamExceptionHandler.class, ProductionExceptionHandler.class, CuppingSessionsExceptionHandler.class})
public class CucumberSpringConfiguration {}
