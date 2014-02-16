package sample.pivot.groovy

import griffon.core.artifact.GriffonView
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonView)
class SampleView {
    FactoryBuilderSupport builder                                                              //<1>

    void initUI() {
        builder.with {
            application(title: application.configuration['application.title'],
                        id: 'mainWindow', maximized: true) {                                   //<2>
                vbox(styles: "{horizontalAlignment:'center', verticalAlignment:'center'}") {
                    label(application.messageSource.getMessage('name.label'))
                    textInput(id: 'input')
                    button(id: 'sayHelloButton', sayHelloAction)                               //<3>
                    textInput(id: 'output', editable: false)
                }
            }
        }
    }
}
