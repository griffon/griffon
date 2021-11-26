package \${package}

import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember;
import griffon.metadata.ArtifactProviderFor
import java.beans.PropertyChangeListener
import javax.annotation.Nonnull

@ArtifactProviderFor(GriffonView)
class _APPView {
    @MVCMember @Nonnull
    FactoryBuilderSupport builder
    @MVCMember @Nonnull
    _APPModel model

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