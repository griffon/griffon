package sample

import griffon.core.addon.GriffonAddon
import griffon.core.controller.ActionInterceptor
import griffon.core.event.EventHandler
import griffon.core.injection.Module
import org.codehaus.griffon.runtime.core.injection.AbstractModule
import org.kordamp.jipsy.ServiceProviderFor

@ServiceProviderFor(Module)
class ApplicationModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        bind(ActionInterceptor)
            .to(TracerActionInterceptor)
            .asSingleton()

        bind(EventHandler)
            .to(Events)
            .asSingleton()

        bind(GriffonAddon)
            .to(ApplicationAddon)
            .asSingleton()
    }
}
