root {
    'groovy.swing.SwingBuilder' {
        controller = ['Threading','Binding','SupportNodes']
        view = '*'
    }
    'griffon.app.ApplicationBuilder' {
        view = '*'
    }
}

features {
    attributeDelegates = [
        greet.GreetController.buttonMarginDelegate
    ]
}
