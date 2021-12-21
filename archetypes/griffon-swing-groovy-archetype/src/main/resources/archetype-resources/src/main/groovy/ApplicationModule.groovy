package \${package}

import griffon.core.event.XEventHandler
import griffon.core.injection.Module
import org.codehaus.griffon.runtime.core.injection.AbstractModule
import org.kordamp.jipsy.annotations.ServiceProviderFor

@ServiceProviderFor(Module)
class ApplicationModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        bind(XEventHandler)
            .to(ApplicationEventHandler)
            .asSingleton()
    }
}