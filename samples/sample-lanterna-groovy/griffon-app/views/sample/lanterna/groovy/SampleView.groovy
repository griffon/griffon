package sample.lanterna.groovy

import griffon.core.artifact.GriffonView
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonView)
class SampleView {
    FactoryBuilderSupport builder                                                              //<1>

    void initUI() {
        builder.with {
            application(id: 'mainWindow') {                                                    //<2>
                verticalLayout()
                label(application.messageSource.getMessage('name.label'))
                textBox(id: 'input')
                button(sayHelloAction)                                                         //<3>
                label(id: 'output')
            }
        }
    }
}
