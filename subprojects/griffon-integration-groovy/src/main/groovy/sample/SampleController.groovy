package sample

import griffon.core.GriffonApplication
import griffon.core.artifact.GriffonController
import org.codehaus.griffon.core.compile.ArtifactProviderFor
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController

import javax.annotation.Nonnull
import javax.inject.Inject
import java.awt.event.ActionEvent

@ArtifactProviderFor(GriffonController)
class SampleController extends AbstractGriffonController {
    SampleModel model

    @Inject
    private SampleService sampleService

    @Inject
    SampleController(@Nonnull GriffonApplication application) {
        super(application)
    }

    void click(ActionEvent event) {
        println(sampleService)
        println("click $event")
        println(model.color)
        println(model.color2)
        println(application.messageSource.getMessage('sample.key'))
    }
}
