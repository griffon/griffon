application {
    title = 'JavaFX Browser'
    startupGroups = ['browser']
    autoShutdown = true
}
mvcGroups {
    // MVC Group for "browser"
    'browser' {
        model      = 'browser.BrowserModel'
        view       = 'browser.BrowserView'
        controller = 'browser.BrowserController'
        wirings    = 'browser.BrowserWirings'
    }
}