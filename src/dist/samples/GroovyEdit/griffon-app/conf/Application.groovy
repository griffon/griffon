application {
    title = 'GroovyEdit'
    startupGroups = ['GroovyEdit']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    'GroovyEdit' {
        model = 'groovyedit.GroovyEditModel'
        controller = 'groovyedit.GroovyEditController'
        view = 'groovyedit.GroovyEditView'
    }
    'FilePanel' {
        model = 'groovyedit.FilePanelModel'
        controller = 'groovyedit.FilePanelController'
        view = 'groovyedit.FilePanelView'
    }
}
