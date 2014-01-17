package editor

import griffon.core.artifact.GriffonView
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonView)
class EditorView {
    def builder
    def model

    void initUI() {
        builder.with {
            tabbedPane(tabGroup, selectedIndex: tabGroup.tabCount) {
                scrollPane(title: tabName, id: 'tab', clientProperties: [mvcIdentifier: mvcIdentifier]) {
                    textArea(id: 'editor', text: bind { model.document.contents })
                }
            }
            bean(model.document, dirty: bind { editor.text != model.document.contents })
        }
    }
}
