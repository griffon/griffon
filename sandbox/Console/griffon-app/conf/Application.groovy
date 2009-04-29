application {
    title='Console'
    startupGroups = ['Console']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    // MVC Group for "org.codehaus.griffon.console.TextEditorPreferences"
    TextEditorPreferences {
        model = 'org.codehaus.griffon.console.TextEditorPreferencesModel'
        view = 'org.codehaus.griffon.console.TextEditorPreferencesView'
        controller = 'org.codehaus.griffon.console.TextEditorPreferencesController'
    }

    // MVC Group for "Console"
    Console {
        model = 'ConsoleModel'
        view = 'ConsoleView'
        controller = 'ConsoleController'
    }
}
