package \${groupId}

import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import java.beans.PropertyChangeListener
import javax.annotation.Nonnull

@ArtifactProviderFor(GriffonView)
class AppView {
    @MVCMember @Nonnull
    FactoryBuilderSupport builder
    @MVCMember @Nonnull
    AppModel model

    void mvcGroupInit(Map<String, Object> args) {
        model.addPropertyChangeListener('clickCount', { evt ->
            builder.clickLabel.text = model.clickCount
        } as PropertyChangeListener)
        builder.clickLabel.text = model.clickCount
    }

    void initUI() {
        builder.with {
            application(id: 'mainWindow') {
                vbox {
                    label(id: 'clickLabel')
                    button(clickAction)
                }
            }
        }
    }
}