package sample;

import griffon.core.addon.GriffonAddon;
import griffon.core.controller.ActionInterceptor;
import griffon.core.event.EventHandler;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;

public class ApplicationModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        bind(ActionInterceptor.class)
            .to(TracerActionInterceptor.class)
            .asSingleton();

        bind(EventHandler.class)
            .to(Events.class)
            .asSingleton();

        bind(GriffonAddon.class)
            .to(ApplicationAddon.class)
            .asSingleton();
    }
}
