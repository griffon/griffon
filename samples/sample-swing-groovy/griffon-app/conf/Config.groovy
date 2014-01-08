application {
    title = 'Swing + Groovy'
    startupGroups = ['sample']
    autoShutdown = true
}
mvcGroups {
    // MVC Group for "sample"
    'sample' {
        model      = 'sample.SampleModel'
        view       = 'sample.SampleView'
        controller = 'sample.SampleController'
    }
}