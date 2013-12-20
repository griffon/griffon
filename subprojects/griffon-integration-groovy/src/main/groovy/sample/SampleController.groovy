package sample

import griffon.core.GriffonApplication
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController

import javax.annotation.Nonnull
import javax.inject.Inject
import java.awt.event.ActionEvent

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
