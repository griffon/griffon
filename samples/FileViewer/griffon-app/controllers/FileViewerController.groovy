import javax.swing.JFileChooser

class FileViewerController {
    def view
    def model

    def openFile = { evt = null ->
    	File f = new File(model.fileName)
    	// create the new MVC Group
    	createMVCGroup('FilePanel', f.path,
    	    [file: f, filesPane:view.filesPane, tabName:f.name])
    }

    def browse = { evt = null ->
    	def openResult = view.fileChooserWindow.showOpenDialog(view.fileViewerFrame)
    	if (JFileChooser.APPROVE_OPTION == openResult) {
    		model.fileName = view.fileChooserWindow.selectedFile.toString()
    		view.textBinding.reverseUpdate()
    	}
    }
}