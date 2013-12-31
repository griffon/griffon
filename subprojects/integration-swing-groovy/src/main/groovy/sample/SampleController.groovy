package sample

import griffon.core.artifact.GriffonController
import org.codehaus.griffon.core.compile.ArtifactProviderFor

import javax.inject.Inject
import java.awt.event.ActionEvent

@ArtifactProviderFor(GriffonController)
class SampleController {
    SampleModel model

    @Inject
    private SampleService sampleService

    void click(ActionEvent event) {
        println(sampleService)
        println("click $event")
        println(model.color)
        println(model.color2)
        println(application.messageSource.getMessage('sample.key'))
    }
}
