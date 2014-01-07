package sample

import griffon.core.artifact.GriffonController
import org.codehaus.griffon.core.compile.ArtifactProviderFor

import javax.inject.Inject
import javax.swing.JOptionPane

@ArtifactProviderFor(GriffonController)
class SampleController {
    SampleModel model                                                    //<1>

    @Inject
    private SampleService sampleService                                  //<2>

    void sayHello() {                                                    //<3>
        String result = sampleService.sayHello(model.input)
        runInsideUIAsync {                                               //<4>
            JOptionPane.showMessageDialog(
                application.windowManager.startingWindow,
                result,
                application.messageSource.getMessage('dialog.title', 'Hello'),
                JOptionPane.INFORMATION_MESSAGE
            )
        }
    }
}
