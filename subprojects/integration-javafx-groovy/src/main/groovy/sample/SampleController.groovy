package sample

import griffon.core.artifact.GriffonController
import javafx.event.ActionEvent
import org.codehaus.griffon.core.compile.ArtifactProviderFor

import javax.inject.Inject

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
