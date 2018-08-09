package ${project_package}

import griffon.annotations.core.Nonnull
import griffon.annotations.inject.MVCMember
import griffon.core.artifact.GriffonView
import org.kordamp.jipsy.ServiceProviderFor
import javax.swing.SwingConstants
import org.codehaus.griffon.runtime.swing.artifact.AbstractSwingGriffonView

@ServiceProviderFor(GriffonView)
class ${project_class_name}View extends AbstractSwingGriffonView {
    @MVCMember @Nonnull
    FactoryBuilderSupport builder

    @MVCMember @Nonnull
    ${project_class_name}Model model

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