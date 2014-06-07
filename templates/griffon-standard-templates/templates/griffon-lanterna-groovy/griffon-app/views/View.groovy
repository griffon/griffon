package ${project_package}

import griffon.core.artifact.GriffonView
import griffon.metadata.ArtifactProviderFor
import java.beans.PropertyChangeListener

@ArtifactProviderFor(GriffonView)
class ${project_capitalized_name}View {
    FactoryBuilderSupport builder
    ${project_capitalized_name}Model model

    void mvcGroupInit(Map<String, Object> args) {
        model.addPropertyChangeListener('clickCount', { evt ->
            builder.clickLabel.text = model.clickCount
        } as PropertyChangeListener)
        builder.clickLabel.text = model.clickCount
    }

    void initUI() {
        builder.with {
            application(id: 'mainWindow') {
                verticalLayout()
                label(id: 'clickLabel')
                button(clickAction)
            }
        }
    }
}