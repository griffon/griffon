root {
    'groovy.swing.SwingBuilder' {
        controller = ['Threading']
        view = '*'
    }
    'griffon.app.ApplicationBuilder' {
        view = '*'
    }
}

jx {
    'groovy.swing.SwingXBuilder' {
        view = '*'
    }
}

root.'griffon.builder.jide.JideBuilder'.view = '*'
root.'griffon.builder.css.CSSBuilder'.view = '*'
// root.'griffon.builder.flamingo.FlamingoBuilder'.view = '*'
// root.'griffon.builder.tray.TrayBuilder'.view = '*'
root.'griffon.builder.trident.TridentBuilder'.view = '*'
