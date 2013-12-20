package sample

import griffon.core.GriffonApplication
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonService

import javax.annotation.Nonnull
import javax.annotation.PreDestroy
import javax.inject.Inject

class SampleService extends AbstractGriffonService {
    @Inject
    SampleService(@Nonnull GriffonApplication application) {
        super(application)
    }

    @PreDestroy
    void destroy() {
        println('Goodbye cruel world!')
    }
}
