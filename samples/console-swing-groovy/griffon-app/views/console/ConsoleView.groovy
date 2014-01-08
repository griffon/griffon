package console

import griffon.core.artifact.GriffonView
import org.codehaus.griffon.core.compile.ArtifactProviderFor

@ArtifactProviderFor(GriffonView)
class ConsoleView {
    def builder                                                              //<1>
    def model                                                                //<1>

    void initUI() {
        builder.with {
            actions {
                action(executeScriptAction,                                  //<2>
                    enabled: bind { model.enabled })
            }

            application(title: application.applicationConfiguration['application.title'],
                pack: true, locationByPlatform: true,
                iconImage: imageIcon('/griffon-icon-48x48.png').image,
                iconImages: [imageIcon('/griffon-icon-48x48.png').image,
                    imageIcon('/griffon-icon-32x32.png').image,
                    imageIcon('/griffon-icon-16x16.png').image]) {
                panel(border: emptyBorder(6)) {
                    borderLayout()

                    scrollPane(constraints: CENTER) {
                        textArea(text: bind(target: model, 'scriptSource'),  //<3>
                            enabled: bind { model.enabled },                 //<2>
                            columns: 40, rows: 10)
                    }

                    hbox(constraints: SOUTH) {
                        button(executeScriptAction)                          //<4>
                        hstrut 5
                        label 'Result:'
                        hstrut 5
                        label text: bind { model.scriptResult }              //<5>
                    }
                }
            }
        }
    }
}
