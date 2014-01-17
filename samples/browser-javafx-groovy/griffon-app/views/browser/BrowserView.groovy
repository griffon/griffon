package browser

import griffon.core.artifact.GriffonView
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonView)
class BrowserView {
    def builder
    def model

    void initUI() {
        builder.application(title: application.applicationConfiguration['application.title'],
            width: 800, height: 600, centerOnScreen: true) {
            scene {
                vbox {
                    hbox {
                        toolBar(hgrow: 'always') {
                            button(backAction, skipName: true)
                            button(forwardAction, skipName: true)
                            button(reloadAction, skipName: true)
                            textField(id: 'urlField', prefColumnCount: 56,
                                onAction: controller.openUrl)
                            actions { bean(model, url: bind(urlField.textProperty())) }
                        }
                    }

                    webView(id: 'browser')

                    label(text: bind(model.statusProperty))
                }
            }
        }
    }
}
