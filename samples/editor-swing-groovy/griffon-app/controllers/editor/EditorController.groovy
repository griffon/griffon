package editor

import griffon.core.artifact.GriffonController
import org.codehaus.griffon.core.compile.ArtifactProviderFor

@ArtifactProviderFor(GriffonController)
class EditorController {
    def model
    def builder

    void mvcGroupInit(Map<String, Object> args) {
        model.document = args.document
        runOutsideUI {
            // load the file's text, outside the EDT
            String text = model.document.file.text
            // update the model inside the EDT
            runInsideUIAsync { model.document.contents = text }
        }
    }

    void saveFile() {
        // write text to file, outside the EDT
        model.document.file.text = builder.editor.text
        // update model.text, inside EDT
        runInsideUIAsync { model.document.contents = builder.editor.text }
    }

    void closeFile() {
        // remove tab
        builder.tabGroup.remove builder.tab
        // cleanup
        destroyMVCGroup model.mvcIdentifier
    }
}
