package \${package}

import griffon.core.event.EventHandler
import griffon.core.injection.Module
import org.codehaus.griffon.runtime.core.injection.AbstractModule
import org.kordamp.jipsy.ServiceProviderFor

@ServiceProviderFor(Module)
class ApplicationModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        bind(EventHandler)
            .to(ApplicationEventHandler)
            .asSingleton()
    }
}