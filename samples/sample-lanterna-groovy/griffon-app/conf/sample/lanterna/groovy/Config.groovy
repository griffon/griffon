package sample.lanterna.groovy

application {
    title = 'Lanterna + Groovy'
    startupGroups = ['sample']
    autoShutdown = true
}
mvcGroups {
    // MVC Group for "sample"
    'sample' {
        view       = 'sample.lanterna.groovy.SampleView'
        controller = 'sample.lanterna.groovy.SampleController'
    }
}