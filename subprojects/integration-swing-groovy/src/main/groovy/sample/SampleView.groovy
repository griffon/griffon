package sample

import griffon.core.artifact.GriffonView
import org.codehaus.griffon.core.compile.ArtifactProviderFor

import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE

@ArtifactProviderFor(GriffonView)
class SampleView {
    FactoryBuilderSupport builder

    void initUI() {
        builder.application(title: 'Main', id: 'mainWindow', size: [320, 240],
            defaultCloseOperation: DO_NOTHING_ON_CLOSE) {
            gridLayout(cols: 2, rows: 2)
            button(clickAction)
            button(clickAction)
            button(clickAction)
            button(clickAction)
        }
    }
}
