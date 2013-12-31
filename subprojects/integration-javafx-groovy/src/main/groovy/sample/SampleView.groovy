package sample

import griffon.core.artifact.GriffonView
import org.codehaus.griffon.core.compile.ArtifactProviderFor


@ArtifactProviderFor(GriffonView)
class SampleView {
    FactoryBuilderSupport builder

    void initUI() {
        builder.application(title: 'Main', sizeToScene: true, centerOnScreen: true) {
            scene(fill: WHITE, width: 320, height: 240) {
                gridPane(hgap: 5, vgap: 5) {
                    button(clickAction, row: 0, column: 0)
                    button(clickAction, row: 0, column: 1)
                    button(clickAction, row: 1, column: 0)
                    button(clickAction, row: 1, column: 1)
                }
            }
        }
    }
}
