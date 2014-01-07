package editor

import griffon.core.artifact.GriffonController
import org.codehaus.griffon.core.compile.ArtifactProviderFor

import javax.swing.JFileChooser

@ArtifactProviderFor(GriffonController)
class ContainerController {
    def model
    def builder

    void open() {
        def window = application.windowManager.startingWindow
        def openResult = builder.fileChooserWindow.showOpenDialog(window)
        if (JFileChooser.APPROVE_OPTION == openResult) {
            File file = new File(builder.fileChooserWindow.selectedFile.toString())
            // let's calculate an unique id for the next mvc group
            String mvcIdentifier = file.name + '-' + System.currentTimeMillis()
            createMVCGroup('editor', mvcIdentifier, [
                document: new Document(file: file, title: file.name),
                tabGroup: builder.tabGroup,
                tabName: file.name,
                mvcIdentifier: mvcIdentifier])
        }
    }

    void save() {
        resolveEditorController().saveFile()
    }

    void close() {
        resolveEditorController()?.closeFile()
    }

    void quit() {
        application.shutdown()
    }

    private GriffonController resolveEditorController() {
        application.mvcGroupManager.controllers[model.mvcIdentifier]
    }
}
