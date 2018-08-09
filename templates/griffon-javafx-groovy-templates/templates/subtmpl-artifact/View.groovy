package ${project_package}

import griffon.annotations.core.Nonnull
import griffon.annotations.inject.MVCMember
import griffon.core.artifact.GriffonView
import griffon.metadata.ArtifactProviderFor
import org.codehaus.griffon.runtime.javafx.artifact.AbstractJavaFXGriffonView

@ArtifactProviderFor(GriffonView)
class ${project_class_name}View extends AbstractJavaFXGriffonView {
    @MVCMember @Nonnull
    FactoryBuilderSupport builder

    @MVCMember @Nonnull
    ${project_class_name}Controller controller

    @MVCMember @Nonnull
    ${project_class_name}Model model

    void initUI() {
        builder.application(title: application.configuration['application.title'],
            sizeToScene: true, centerOnScreen: true, name: '${name}') {
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