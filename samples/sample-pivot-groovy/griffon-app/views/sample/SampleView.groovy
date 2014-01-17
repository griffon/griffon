package sample

import griffon.core.artifact.GriffonView
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonView)
class SampleView {
    FactoryBuilderSupport builder                                                              //<1>

    void initUI() {
        builder.with {
            application(title: application.applicationConfiguration['application.title'],
                        id: 'mainWindow', maximized: true) {                                   //<2>
                vbox(styles: "{horizontalAlignment:'center', verticalAlignment:'center'}") {
                    label(application.messageSource.getMessage('name.label'))
                    textInput(id: 'input')
                    button(sayHelloAction)                                                     //<3>
                    textInput(id: 'output', editable: false)
                }
            }
        }
    }
}
