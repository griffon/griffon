package editor

import griffon.core.artifact.GriffonModel
import griffon.transform.PropertyListener
import groovy.beans.Bindable
import griffon.metadata.ArtifactProviderFor

import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

@ArtifactProviderFor(GriffonModel)
class ContainerModel implements ChangeListener {
    final DocumentModel documentModel = new DocumentModel()

    @Bindable
    @PropertyListener(mvcUpdater)
    String mvcIdentifier

    // listens to changes on the mvcId property
    private mvcUpdater = { e ->
        Document document = null
        if (e.newValue) {
            document = application.mvcGroupManager.models[mvcIdentifier].document
        } else {
            document = new Document()
        }
        documentModel.document = document
    }

    // listens to tab selection; updates the mvcId property
    void stateChanged(ChangeEvent e) {
        int selectedIndex = e.source.selectedIndex
        if (selectedIndex < 0) {
            setMvcIdentifier(null)
        } else {
            def tab = e.source[selectedIndex]
            setMvcIdentifier(tab.getClientProperty('mvcIdentifier'))
        }
    }
}
