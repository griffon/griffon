package sample;

import griffon.core.controller.ActionInterceptor;
import griffon.core.injection.Module;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.kordamp.jipsy.ServiceProviderFor;

@ServiceProviderFor(Module.class)
public class ApplicationModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        bind(ActionInterceptor.class)
            .to(TracerActionInterceptor.class)
            .asSingleton();
    }
}
