import groovy.beans.Bindable

@Bindable class FilePanelModel {
   File loadedFile
   long lastModified
   String fileText
}