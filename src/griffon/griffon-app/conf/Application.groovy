mvcGroups {
    root {
        model = '@griffon.app.class.name@Model'
        view = '@griffon.app.class.name@View'
        controller = '@griffon.app.class.name@Controller'
    }
}

application {
    title="@griffon.app.class.name@"

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}