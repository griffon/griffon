package ${project_package}

import griffon.core.artifact.GriffonView
import griffon.metadata.ArtifactProviderFor
import java.beans.PropertyChangeListener

@ArtifactProviderFor(GriffonView)
class ${project_class_name}View {
    FactoryBuilderSupport builder
    ${project_class_name}Model model

    void mvcGroupInit(Map<String, Object> args) {
        model.addPropertyChangeListener('clickCount', { evt ->
            builder.clickLabel.text = model.clickCount
        } as PropertyChangeListener)
        builder.clickLabel.text = model.clickCount
    }

    void initUI() {
        builder.with {
            application(title: application.configuration['application.title'],
                id: 'mainWindow', maximized: true) {
                vbox(styles: "{horizontalAlignment:'center', verticalAlignment:'center'}") {
                    label(id: 'clickLabel')
                    button(clickAction)
                }
            }
        }
    }
}