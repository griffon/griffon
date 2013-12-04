package sample;

import griffon.core.controller.ActionInterceptor;
import griffon.core.event.EventHandler;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.codehaus.griffon.runtime.core.injection.NamedImpl;

public class ApplicationModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        bind(ActionInterceptor.class)
            .withClassifier(new NamedImpl("tracer"))
            .to(TracerActionInterceptor.class)
            .asSingleton();

        bind(EventHandler.class)
            .to(Events.class)
            .asSingleton();
    }
}
