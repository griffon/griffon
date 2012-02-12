application {
    title = 'Sample'
    startupGroups = ['sample']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    // MVC Group for "extended"
    'extended' {
        model = 'sample.ExtendedModel'
        controller = 'sample.ExtendedController'
        view = 'sample.ExtendedView'
    }

    // MVC Group for "abstract"
    'abstract' {
        model = 'sample.AbstractModel'
        controller = 'sample.AbstractController'
        view = 'sample.AbstractView'
    }

    // MVC Group for "sample"
    'sample' {
        model = 'sample.SampleModel'
        controller = 'sample.SampleController'
        view = 'sample.SampleView'
    }
}
