package sample

import griffon.core.artifact.GriffonController
import griffon.metadata.ArtifactProviderFor

import javax.inject.Inject

@ArtifactProviderFor(GriffonController)
class SampleController {
    FactoryBuilderSupport builder                                          //<1>

    @Inject
    private SampleService sampleService                                    //<2>

    void sayHello() {                                                      //<3>
        String result = sampleService.sayHello(builder.input.text)
        runInsideUIAsync {                                                 //<4>
            builder.output.text = result
        }
    }
}
