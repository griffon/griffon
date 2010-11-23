application {
    title='SwingPad'
    startupGroups = ['root','Script']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    // MVC Group for "root"
    root {
        model = 'SwingPadModel'
        view = 'SwingPadView'
        controller = 'SwingPadController'
    }

    // MVC Group for "Script"
    Script {
        model = 'ScriptModel'
        view = 'ScriptView'
        controller = 'ScriptController'
    }
}