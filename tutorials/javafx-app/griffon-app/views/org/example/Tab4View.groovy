package org.example

import griffon.core.artifact.GriffonView
import griffon.javafx.support.fontawesome.FontAwesomeIcon
import griffon.metadata.ArtifactProviderFor
import griffon.plugins.fontawesome.FontAwesome
import javafx.scene.control.Tab
import org.codehaus.griffon.runtime.javafx.artifact.AbstractJavaFXGriffonView

@ArtifactProviderFor(GriffonView)
class Tab4View extends AbstractJavaFXGriffonView {
    FactoryBuilderSupport builder
    SampleController controller
    SampleModel model
    AppView parentView

    void initUI() {
        builder.with {
            content = builder.fxml(resource('/org/example/tab4.fxml')) {
                inputLabel.text = application.messageSource.getMessage('name.label')
                bean(input, text: bind(model.inputProperty))
                bean(output, text: bind(model.outputProperty))
            }
        }

        connectActions(builder.content, controller)

        Tab tab = new Tab('Hybrid')
        tab.graphic = new FontAwesomeIcon(FontAwesome.FA_ROCKET)
        tab.content = builder.content
        tab.closable = false
        parentView.tabPane.tabs.add(tab)
    }
}
