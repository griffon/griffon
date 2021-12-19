package \${package}

import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import org.codehaus.griffon.runtime.javafx.artifact.AbstractJavaFXGriffonView
import griffon.annotations.core.Nonnull

@ArtifactProviderFor(GriffonView)
class _APPView extends AbstractJavaFXGriffonView {
    @MVCMember @Nonnull
    FactoryBuilderSupport builder
    @MVCMember @Nonnull
    _APPController controller
    @MVCMember @Nonnull
    _APPModel model

    void initUI() {
        builder.application(title: application.configuration['application.title'],
            sizeToScene: true, centerOnScreen: true, name: 'mainWindow') {
            scene(fill: WHITE, width: 200, height: 60) {
                g = gridPane {
                    label(id: 'clickLabel', row: 0, column: 0,
                          text: bind(model.clickCountProperty()))
                    button(row: 1, column: 0, prefWidth: 200,
                           griffonActionId: 'click', id: 'click')
                }
                connectActions(g, controller)
                connectMessageSource(g)
            }
        }
    }
}