mvcGroups {
    root {
        model = 'WordFinderModel'
        view = 'WordFinderView'
        controller = 'WordFinderController'
    }
}

application {
    title="WordFinder"

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
// The following properties have been added by the Upgrade process...
application.startupGroups=['root'] // default startup group from 0.0
