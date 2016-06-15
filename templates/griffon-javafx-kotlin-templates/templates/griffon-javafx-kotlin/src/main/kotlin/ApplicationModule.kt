package ${project_package}

import griffon.core.event.EventHandler
import griffon.core.injection.Module
import org.codehaus.griffon.runtime.core.injection.AbstractModule
import org.kordamp.jipsy.ServiceProviderFor

@ServiceProviderFor(Module::class)
class ApplicationModule : AbstractModule() {
    override fun doConfigure() {
        bind(EventHandler::class.java)
            .to(ApplicationEventHandler::class.java)
            .asSingleton()
    }
}