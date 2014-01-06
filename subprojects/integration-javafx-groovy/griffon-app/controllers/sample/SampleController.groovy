package sample

import griffon.core.artifact.GriffonController
import javafx.event.ActionEvent
import org.codehaus.griffon.core.compile.ArtifactProviderFor

import javax.inject.Inject

@ArtifactProviderFor(GriffonController)
class SampleController {
    SampleModel model                                                     // <1>

    @Inject
    private SampleService sampleService                                   // <2>

    void sayHello(ActionEvent event) {                                    // <3>
        String result = sampleService.sayHello(model.input)
        println(result)
    }
}
