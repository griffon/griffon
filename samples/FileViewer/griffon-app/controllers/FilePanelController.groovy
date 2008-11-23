class FilePanelController {
    def model

    void mvcGroupInit(Map args) {
        // load the file and fire updates
        model.loadedFile = args.file
        updateFile()
    }

    def updateFile = { evt = null ->
        if (model.loadedFile.lastModified() > model.lastModified) {
            model.fileText = model.loadedFile.text
            model.lastModified = model.loadedFile.lastModified()
        }
    }
}