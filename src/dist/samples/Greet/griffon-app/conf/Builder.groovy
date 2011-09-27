
root {
    'groovy.swing.SwingBuilder' {
        controller = ['Threading','Binding','SupportNodes']
        view = '*'
    }
}

root.'SwingGriffonAddon'.addon=true

features {
    attributeDelegates = [
        greet.GreetController.buttonMarginDelegate
    ]
}
