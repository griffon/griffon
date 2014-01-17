package sample

import griffon.core.artifact.GriffonController
import griffon.metadata.ArtifactProviderFor

import javax.inject.Inject

@ArtifactProviderFor(GriffonController)
class SampleController {
    SampleModel model                                                      //<1>

    @Inject
    private SampleService sampleService                                    //<2>

    void sayHello() {                                                      //<3>
        String result = sampleService.sayHello(model.input)
        model.output = result                                              //<4>
    }
}
