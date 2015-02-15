package org.example.calculator;

import griffon.core.addon.GriffonAddon;
import griffon.core.injection.Module;
import griffon.inject.DependsOn;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.kordamp.jipsy.ServiceProviderFor;

import javax.inject.Named;

@DependsOn("swing")
@Named("calculator")
@ServiceProviderFor(Module.class)
public class CalculatorModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        bind(GriffonAddon.class)
            .to(CalculatorAddon.class)
            .asSingleton();
    }
}
