package groovyedit

// auto import only works with Griffon artifacts
import groovy.beans.Bindable
import griffon.beans.Listener
import java.beans.PropertyChangeListener

class DocumentProxy extends Document {
    @Bindable @Listener(documentUpdater)
    Document document = new Document()

    // copies one property value from document to itself
    private proxyUpdater = { e ->
        // owner is a standard property found in closures
        // it points to the instance that contains the closure
        // i.e, the DocumentProxy instance that holds this closure
        owner[e.propertyName] = e.newValue
    } as PropertyChangeListener

    // listens to changes on the document property
    // copies all properties form source to itself
    private documentUpdater = { e ->
        e.oldValue?.removePropertyChangeListener(proxyUpdater)
        e.newValue?.addPropertyChangeListener(proxyUpdater)
        e.newValue?.copyTo(owner)
    }
}
