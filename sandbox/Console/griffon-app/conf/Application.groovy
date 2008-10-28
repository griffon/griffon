application {
    title='Console'
    startupGroups = ['Console']

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}

// MVC Group for "Console"
mvcGroups {
    Console {
        model = 'ConsoleModel'
        view = 'ConsoleView'
        controller = 'ConsoleController'
    }
}
