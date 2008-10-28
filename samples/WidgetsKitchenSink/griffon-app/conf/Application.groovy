

mvcGroups {
    root {
        model = 'WidgetsKitchenSinkModel'
        view = 'WidgetsKitchenSinkView'
        controller = 'WidgetsKitchenSinkController'
    }
}

//application.frameClass = 'javax.swing.JFrame'
application.title="Widgets Kitchen Sink"

// The following properties have been added by the Upgrade process...
application.startupGroups=['root'] // default startup group from 0.0
