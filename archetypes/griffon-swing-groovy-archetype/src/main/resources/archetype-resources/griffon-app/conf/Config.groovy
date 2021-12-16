application {
    title = '_app'
    startupGroups = ['_app']
    autoShutdown = true
}
mvcGroups {
    // MVC Group for "_app"
    '_app' {
        model      = '\${package}._APPModel'
        view       = '\${package}._APPView'
        controller = '\${package}._APPController'
    }
}