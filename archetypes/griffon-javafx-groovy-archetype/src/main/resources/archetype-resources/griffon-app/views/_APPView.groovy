package \${package}

import griffon.annotations.core.Nonnull
import griffon.annotations.inject.MVCMember
import griffon.core.artifact.GriffonView
import org.codehaus.griffon.runtime.javafx.artifact.AbstractJavaFXGriffonView
import org.kordamp.jipsy.annotations.ServiceProviderFor

@ServiceProviderFor(GriffonView)
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