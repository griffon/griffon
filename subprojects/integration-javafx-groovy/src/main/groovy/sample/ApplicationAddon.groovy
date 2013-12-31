package sample

import griffon.core.GriffonApplication
import griffon.inject.DependsOn
import org.codehaus.griffon.runtime.core.addon.AbstractGriffonAddon

import javax.annotation.Nonnull
import javax.inject.Inject
import javax.inject.Named

@Named('application')
@DependsOn('javafx')
class ApplicationAddon extends AbstractGriffonAddon {
    @Inject
    ApplicationAddon(@Nonnull GriffonApplication application) {
        super(application)
    }

    void onStartupStart(GriffonApplication application) {
        System.out.println('Application is entering STARTUP phase')
    }
}
