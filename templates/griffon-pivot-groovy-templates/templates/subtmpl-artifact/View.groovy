package ${project_package}

import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember;
import griffon.metadata.ArtifactProviderFor
import java.beans.PropertyChangeListener
import griffon.annotations.core.Nonnull

@ArtifactProviderFor(GriffonView)
class ${project_class_name}View {
    @MVCMember @Nonnull
    FactoryBuilderSupport builder
    @MVCMember @Nonnull
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
                id: '${name}', maximized: true) {
                vbox(styles: "{horizontalAlignment:'center', verticalAlignment:'center'}") {
                    label(id: 'clickLabel')
                    button(clickAction)
                }
            }
        }
    }
}