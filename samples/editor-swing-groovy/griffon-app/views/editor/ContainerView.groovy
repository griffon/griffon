package editor

import griffon.core.artifact.GriffonView
import griffon.metadata.ArtifactProviderFor

import java.awt.BorderLayout

import static griffon.util.GriffonApplicationUtils.isMacOSX

@ArtifactProviderFor(GriffonView)
class ContainerView {
    def builder
    def model

    void initUI() {
        builder.with {
            actions {
                action(saveAction,
                    enabled: bind { model.documentModel.dirty })
            }

            fileChooser(id: 'fileChooserWindow')
            application(title: application.getConfiguration['application.title'],
                size: [480, 320], locationByPlatform: true,
                iconImage: imageIcon('/griffon-icon-48x48.png').image,
                iconImages: [imageIcon('/griffon-icon-48x48.png').image,
                    imageIcon('/griffon-icon-32x32.png').image,
                    imageIcon('/griffon-icon-16x16.png').image]) {
                menuBar {
                    menu('File') {
                        menuItem(openAction)
                        menuItem(closeAction)
                        separator()
                        menuItem(saveAction)
                        if (!isMacOSX) {
                            separator()
                            menuItem(quitAction)
                        }
                    }
                }

                borderLayout()
                tabbedPane(id: 'tabGroup', constraints: BorderLayout.CENTER)
                noparent {
                    tabGroup.addChangeListener(model)
                }
            }
        }
    }
}
