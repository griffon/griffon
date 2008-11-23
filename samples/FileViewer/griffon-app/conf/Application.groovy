application {
    title='FileViewer'
    startupGroups = ['FileViewer']

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    // MVC Group for "FilePanel"
    FilePanel {
        model = 'FilePanelModel'
        view = 'FilePanelView'
        controller = 'FilePanelController'
    }

    // MVC Group for "FileViewer"
    FileViewer {
        model = 'FileViewerModel'
        view = 'FileViewerView'
        controller = 'FileViewerController'
    }

}
