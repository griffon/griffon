
root {
    'groovy.swing.SwingBuilder' {
        controller = ['Threading']
        view = '*'
    }
}

jx {
    'groovy.swing.SwingXBuilder' {
        view = '*'
    }
}

fx.'groovyx.javafx.SceneGraphBuilder'.view = '*'

root.'GlazedlistsGriffonAddon'.addon=true

root.'griffon.builder.jide.JideBuilder'.view = '*'