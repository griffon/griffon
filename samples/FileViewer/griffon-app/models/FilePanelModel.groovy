import groovy.beans.Bindable

class FilePanelModel {
   @Bindable File loadedFile
   @Bindable long lastModified
   @Bindable String fileText
}