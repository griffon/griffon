package sample.pivot.groovy

application {
    title = 'Pivot + Groovy'
    startupGroups = ['sample']
    autoShutdown = true
}
mvcGroups {
    // MVC Group for "sample"
    'sample' {
        view       = 'sample.pivot.groovy.SampleView'
        controller = 'sample.pivot.groovy.SampleController'
    }
}