package console

import griffon.core.artifact.GriffonController
import org.codehaus.griffon.core.compile.ArtifactProviderFor

@ArtifactProviderFor(GriffonController)
class ConsoleController {
    def model

    private GroovyShell shell = new GroovyShell()

    void executeScript() {
        model.enabled = false
        def result
        try {
            result = shell.evaluate(model.scriptSource)
        } finally {
            model.enabled = true
            model.scriptResult = result
        }
    }
}
