package groovyedit

import javax.swing.JFileChooser

class GroovyEditController {
   def model
   def view

   def openFile = {
      def openResult = view.fileChooserWindow.showOpenDialog(view.fileViewerWindow)
      if(JFileChooser.APPROVE_OPTION == openResult) {
         File file = new File(view.fileChooserWindow.selectedFile.toString())
         // let's calculate an unique id for the next mvc group
         String mvcIdentifier = file.path + '-' + System.currentTimeMillis()
         createMVCGroup('filePanel', mvcIdentifier, [
             document: new Document(file: file, title: file.name),
             tabGroup: view.tabGroup,
             tabName: file.name,
             mvcIdentifier: mvcIdentifier])
      }
   }

   def saveFile = {
      app.controllers[model.mvcIdentifier].saveFile()
   }

   def closeFile = {
      app.controllers[model.mvcIdentifier].closeFile()
   }

   def quit = {
      app.shutdown()
   }
}
