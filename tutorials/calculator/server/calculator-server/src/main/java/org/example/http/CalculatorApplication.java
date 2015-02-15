package org.example.http;

import org.example.calculator.pm.CalculatorPMResource;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/")
public class CalculatorApplication extends ResourceConfig {
    public CalculatorApplication() {
        registerClasses(
            CalculatorPMResource.class
        );
    }
}