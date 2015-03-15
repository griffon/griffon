package org.example

import griffon.core.artifact.GriffonView
import griffon.javafx.support.fontawesome.FontAwesomeIcon
import griffon.metadata.ArtifactProviderFor
import griffon.plugins.fontawesome.FontAwesome
import javafx.scene.control.Tab

@ArtifactProviderFor(GriffonView)
class Tab3View {
    FactoryBuilderSupport builder
    SampleModel model
    AppView parentView

    void initUI() {
        builder.with {
            content = anchorPane {
                label(leftAnchor: 14, topAnchor: 14,
                    text: application.messageSource.getMessage('name.label'))
                textField(leftAnchor: 172, topAnchor: 11, prefWidth: 200,
                    text: bind(model.inputProperty))
                button(leftAnchor: 172, topAnchor: 45, prefWidth: 200,
                    sayHelloAction)
                label(leftAnchor: 14, topAnchor: 80, prefWidth: 200,
                    text: bind(model.outputProperty))
            }
        }

        Tab tab = new Tab('GroovyFX')
        tab.graphic = new FontAwesomeIcon(FontAwesome.FA_FLASH)
        tab.content = builder.content
        tab.closable = false
        parentView.tabPane.tabs.add(tab)
    }
}
