application {
    title = 'Lanterna + Groovy'
    startupGroups = ['sample']
    autoShutdown = true
}
mvcGroups {
    // MVC Group for "sample"
    'sample' {
        view       = 'sample.SampleView'
        controller = 'sample.SampleController'
    }
}