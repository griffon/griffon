application {
    title = 'SwingPad'
    startupGroups = ['SwingPad', 'script']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    // MVC Group for "nodes"
    'nodes' {
        model      = 'griffon.samples.swingpad.NodesModel'
        view       = 'griffon.samples.swingpad.NodesView'
        controller = 'griffon.samples.swingpad.DialogController'
    }

    // MVC Group for "script"
    'script' {
        model      = 'griffon.samples.swingpad.ScriptModel'
        view       = 'griffon.samples.swingpad.ScriptView'
        controller = 'griffon.samples.swingpad.ScriptController'
    }

    // MVC Group for "preferences"
    'preferences' {
        model      = 'griffon.samples.swingpad.PreferencesModel'
        view       = 'griffon.samples.swingpad.PreferencesView'
        controller = 'griffon.samples.swingpad.DialogController'
    }

    // MVC Group for "license"
    'license' {
        model      = 'griffon.samples.swingpad.LicenseModel'
        view       = 'griffon.samples.swingpad.LicenseView'
        controller = 'griffon.samples.swingpad.DialogController'
    }

    // MVC Group for "credits"
    'credits' {
        model      = 'griffon.samples.swingpad.CreditsModel'
        view       = 'griffon.samples.swingpad.CreditsView'
        controller = 'griffon.samples.swingpad.DialogController'
    }

    // MVC Group for "about"
    'about' {
        model      = 'griffon.samples.swingpad.AboutModel'
        view       = 'griffon.samples.swingpad.AboutView'
        controller = 'griffon.samples.swingpad.AboutController'
    }

    // MVC Group for "SwingPad"
    'SwingPad' {
        model      = 'griffon.samples.swingpad.SwingPadModel'
        view       = 'griffon.samples.swingpad.SwingPadView'
        controller = 'griffon.samples.swingpad.SwingPadController'
    }
}
