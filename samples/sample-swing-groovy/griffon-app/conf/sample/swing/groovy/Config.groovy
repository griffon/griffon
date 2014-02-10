package sample.swing.groovy

application {
    title = 'Swing + Groovy'
    startupGroups = ['sample']
    autoShutdown = true
}
mvcGroups {
    // MVC Group for "sample"
    'sample' {
        model      = 'sample.swing.groovy.SampleModel'
        view       = 'sample.swing.groovy.SampleView'
        controller = 'sample.swing.groovy.SampleController'
    }
}