application {
    title = 'Simple Editor'
    startupGroups = ['container']
    autoShutdown = true
}
mvcGroups {
    // MVC Group for "editor"
    'editor' {
        model      = 'editor.EditorModel'
        view       = 'editor.EditorView'
        controller = 'editor.EditorController'
    }
    // MVC Group for "container"
    'container' {
        model      = 'editor.ContainerModel'
        view       = 'editor.ContainerView'
        controller = 'editor.ContainerController'
    }
}