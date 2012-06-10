application {
    title = 'GroovyEdit'
    startupGroups = ['groovyEdit']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    'groovyEdit' {
        model      = 'groovyedit.GroovyEditModel'
        view       = 'groovyedit.GroovyEditView'
        controller = 'groovyedit.GroovyEditController'
    }
    'filePanel' {
        model      = 'groovyedit.FilePanelModel'
        view       = 'groovyedit.FilePanelView'
        controller = 'groovyedit.FilePanelController'
    }
}
