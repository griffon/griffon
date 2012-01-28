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
        model      = 'groovyedit.GroovyEditModel'
        view       = 'groovyedit.GroovyEditView'
        controller = 'groovyedit.GroovyEditController'
    }
    'FilePanel' {
        model      = 'groovyedit.FilePanelModel'
        view       = 'groovyedit.FilePanelView'
        controller = 'groovyedit.FilePanelController'
    }
}
