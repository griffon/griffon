package \${groupId}

import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javax.swing.SwingConstants
import javax.annotation.Nonnull

@ArtifactProviderFor(GriffonView)
class AppView {
    @MVCMember @Nonnull
    FactoryBuilderSupport builder
    @MVCMember @Nonnull
    AppModel model

    void initUI() {
        builder.with {
            application(size: [320, 160], id: 'mainWindow',
                title: application.configuration['application.title'],
                iconImage:   imageIcon('/griffon-icon-48x48.png').image,
                iconImages: [imageIcon('/griffon-icon-48x48.png').image,
                             imageIcon('/griffon-icon-32x32.png').image,
                             imageIcon('/griffon-icon-16x16.png').image]) {
                gridLayout(rows: 2, cols: 1)
                label(id: 'clickLabel', text: bind { model.clickCount },
                     horizontalAlignment: SwingConstants.CENTER)
                button(id: 'clickButton', clickAction)
            }
        }
    }
}