package sample

import griffon.core.artifact.GriffonView
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonView)
class SampleView {
    FactoryBuilderSupport builder                                                              //<1>
    SampleModel model                                                                          //<1>

    void initUI() {
        builder.with {
            application(title: application.configuration['application.title'],                 //<2>
                id: 'mainWindow', size: [320, 160],
                iconImage: imageIcon('/griffon-icon-48x48.png').image,
                iconImages: [imageIcon('/griffon-icon-48x48.png').image,
                    imageIcon('/griffon-icon-32x32.png').image,
                    imageIcon('/griffon-icon-16x16.png').image]) {
                gridLayout(rows: 4, cols: 1)
                label(application.messageSource.getMessage('name.label'))
                textField(text: bind(target: model, 'input'))                                   //<3>
                button(sayHelloAction)                                                          //<4>
                label(text: bind { model.output })                                              //<3>
            }
        }
    }
}
