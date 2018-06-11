application {
    title = 'app'
    startupGroups = ['app']
    autoShutdown = true
}
mvcGroups {
    // MVC Group for "app"
    'app' {
        model      = '\${groupId}.AppModel'
        view       = '\${groupId}.AppView'
        controller = '\${groupId}.AppController'
    }
}