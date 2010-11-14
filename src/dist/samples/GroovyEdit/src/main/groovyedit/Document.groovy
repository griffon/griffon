package groovyedit

// auto import only works with Griffon artifacts
import groovy.beans.Bindable

@Bindable
class Document {
    String title
    String contents
    boolean dirty
    File file

    void copyTo(Document doc) {
       doc.title = title
       doc.contents = contents
       doc.dirty = dirty
       doc.file = file
    }
}