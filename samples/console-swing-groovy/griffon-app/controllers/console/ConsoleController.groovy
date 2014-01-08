package console

import griffon.core.artifact.GriffonController
import org.codehaus.griffon.core.compile.ArtifactProviderFor

@ArtifactProviderFor(GriffonController)
class ConsoleController {
    def model                                            //<1>

    private GroovyShell shell = new GroovyShell()

    void executeScript() {                               //<2>
        model.enabled = false
        def result
        try {
            result = shell.evaluate(model.scriptSource)  //<3>
        } finally {
            model.enabled = true
            model.scriptResult = result                  //<4>
        }
    }
}
