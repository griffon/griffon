import griffon.core.GriffonApplication
import org.codehaus.griffon.runtime.core.AbstractLifecycleHandler

import griffon.annotations.core.Nonnull
import javax.inject.Inject

class Initialize extends AbstractLifecycleHandler {
    @Inject
    Initialize(@Nonnull GriffonApplication application) {
        super(application)
    }

    @Override
    void execute() {
    }
}