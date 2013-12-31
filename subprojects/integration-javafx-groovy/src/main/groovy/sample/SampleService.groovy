package sample

import griffon.core.artifact.GriffonService
import org.codehaus.griffon.core.compile.ArtifactProviderFor

import javax.annotation.PreDestroy

@ArtifactProviderFor(GriffonService)
class SampleService {
    @PreDestroy
    void destroy() {
        println('Goodbye cruel world!')
    }
}
