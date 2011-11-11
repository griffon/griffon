application {
    title = 'Mdi'
    startupGroups = ['mdi']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    // MVC Group for "window"
    'window' {
        model      = 'mdi.WindowModel'
        view       = 'mdi.WindowView'
        controller = 'mdi.WindowController'
    }

    // MVC Group for "mdi"
    'mdi' {
        model      = 'mdi.MdiModel'
        view       = 'mdi.MdiView'
        controller = 'mdi.MdiController'
    }
}