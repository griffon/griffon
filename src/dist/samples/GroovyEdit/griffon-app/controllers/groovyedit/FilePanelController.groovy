package groovyedit

class FilePanelController {
   def model
   def view

   void mvcGroupInit(Map args) {
      model.document = args.document
      execOutsideUI {
         // load the file's text, outside the EDT
         String text = model.document.file.text
         // update the model inside the EDT
         execInsideUIAsync { model.document.contents = text }
      }
   }

   def saveFile = {
      // write text to file, outside the EDT
      model.document.file.text = view.editor.text
      // update model.text, inside EDT
      execInsideUIAsync { model.document.contents = view.editor.text }
   }

   def closeFile = {
      // remove tab
      view.tabGroup.remove view.tab
      // cleanup
      destroyMVCGroup model.mvcId
   }
}
