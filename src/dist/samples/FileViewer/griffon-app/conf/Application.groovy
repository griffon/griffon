application {
    title='FileViewer'
    startupGroups = ['FileViewer']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    // MVC Group for "FilePanel"
    FilePanel {
        model = 'FilePanelModel'
        view = 'FilePanelView'
    }

    // MVC Group for "FileViewer"
    FileViewer {
        model = 'FileViewerModel'
        view = 'FileViewerView'
        controller = 'FileViewerController'
    }

}
