application {
    title = 'GroovyFXPad'
    startupGroups = ['ide']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    // MVC Group for "nodes"
    'nodes' {
        model      = 'griffon.samples.groovyfxpad.NodesModel'
        view       = 'griffon.samples.groovyfxpad.NodesView'
        controller = 'griffon.samples.groovyfxpad.DialogController'
    }

    // MVC Group for "preferences"
    'preferences' {
        model      = 'griffon.samples.groovyfxpad.PreferencesModel'
        view       = 'griffon.samples.groovyfxpad.PreferencesView'
        controller = 'griffon.samples.groovyfxpad.DialogController'
    }

    // MVC Group for "license"
    'license' {
        model      = 'griffon.samples.groovyfxpad.LicenseModel'
        view       = 'griffon.samples.groovyfxpad.LicenseView'
        controller = 'griffon.samples.groovyfxpad.DialogController'
    }

    // MVC Group for "credits"
    'credits' {
        model      = 'griffon.samples.groovyfxpad.CreditsModel'
        view       = 'griffon.samples.groovyfxpad.CreditsView'
        controller = 'griffon.samples.groovyfxpad.DialogController'
    }

    // MVC Group for "about"
    'about' {
        model      = 'griffon.samples.groovyfxpad.AboutModel'
        view       = 'griffon.samples.groovyfxpad.AboutView'
        controller = 'griffon.samples.groovyfxpad.AboutController'
    }

    // MVC Group for "ide"
    'ide' {
        model      = 'griffon.samples.groovyfxpad.IdeModel'
        controller = 'griffon.samples.groovyfxpad.IdeController'
        view       = 'griffon.samples.groovyfxpad.IdeView'
    }
}
