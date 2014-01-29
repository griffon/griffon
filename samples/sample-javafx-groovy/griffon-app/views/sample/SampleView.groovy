package sample

import griffon.core.artifact.GriffonView
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonView)
class SampleView {
    FactoryBuilderSupport builder                                                              //<1>
    SampleModel model                                                                          //<1>

    void initUI() {
        builder.application(title: application.getConfiguration['application.title'],
            sizeToScene: true, centerOnScreen: true) {                                         //<2>
            scene(fill: WHITE, width: 400, height: 120) {
                anchorPane {
                    label(leftAnchor: 14, topAnchor: 11,
                          text: application.messageSource.getMessage('name.label'))
                    textField(leftAnchor: 172, topAnchor: 11, prefWidth: 200,
                              text: bind(model.inputProperty()))                               //<3>
                    button(leftAnchor: 172, topAnchor: 45, prefWidth: 200,
                           sayHelloAction)                                                     //<4>
                    label(leftAnchor: 14, topAnchor: 80, prefWidth: 200,
                        text: bind(model.outputProperty()))                                    //<3>
                }
            }
        }
    }
}
