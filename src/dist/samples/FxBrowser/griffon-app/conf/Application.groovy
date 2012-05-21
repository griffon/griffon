application {
    title = 'FxBrowser'
    startupGroups = ['fx-browser']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    // MVC Group for "fx-browser"
    'fx-browser' {
        model      = 'fx.browser.FxBrowserModel'
        view       = 'fx.browser.FxBrowserView'
        controller = 'fx.browser.FxBrowserController'
        // adding a new MVC member is easy
        wirings    = 'fx.browser.FxBrowserWirings'
    }
}
