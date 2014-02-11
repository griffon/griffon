package sample.javafx.groovy

application {
    title = 'JavaFX + Groovy'
    startupGroups = ['sample']
    autoShutdown = true
}
mvcGroups {
    // MVC Group for "sample"
    'sample' {
        model      = 'sample.javafx.groovy.SampleModel'
        view       = 'sample.javafx.groovy.SampleView'
        controller = 'sample.javafx.groovy.SampleController'
    }
}