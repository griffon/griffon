mvcGroups {
    root {
        model = 'FontPickerModel'
        view = 'FontPickerView'
        controller = 'FontPickerController'
    }
}

application {
    title="FontPicker"

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
// The following properties have been added by the Upgrade process...
application.startupGroups=['root'] // default startup group from 0.0
