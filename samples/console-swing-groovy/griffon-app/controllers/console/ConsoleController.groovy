package console

import griffon.core.artifact.GriffonController
import griffon.metadata.ArtifactProviderFor

import javax.inject.Inject

@ArtifactProviderFor(GriffonController)
class ConsoleController {
    def model                                                //<1>

    @Inject
    Evaluator evaluator                                      //<2>

    void executeScript() {                                   //<3>
        model.enabled = false
        def result
        try {
            result = evaluator.evaluate(model.scriptSource)  //<4>
        } finally {
            model.enabled = true
            model.scriptResult = result                      //<5>
        }
    }
}
