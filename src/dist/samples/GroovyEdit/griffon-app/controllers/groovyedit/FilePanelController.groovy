package groovyedit

class FilePanelController {
   def model
   def view

   void mvcGroupInit(Map args) {
      model.document = args.document
      model.mvcId = args.mvcId
      execOutside {
         // load the file's text, outside the EDT
         String text = model.document.file.text
         // update the model inside the EDT
         execAsync { model.document.contents = text }
      }
   }

   def saveFile = {
      execOutside {
         // write text to file, outside the EDT
         model.document.file.text = view.editor.text
         // update model.text, inside EDT
         execAsync { model.document.contents = view.editor.text }
      }
   }

   def closeFile = {
      // remove tab
      view.tabGroup.remove view.tab
      // cleanup
      destroyMVCGroup model.mvcId
   }
}
