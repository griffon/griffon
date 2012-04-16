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

root.'GlazedlistsGriffonAddon'.addon = true
root.'griffon.builder.gfx.GfxBuilder'.view = '*'
root.'griffon.builder.trident.TridentBuilder'.view = '*'
root.'JGoodiesFormsGriffonAddon'.addon = true
root.'griffon.builder.css.CSSBuilder'.view = '*'
root.'griffon.builder.css.CSSBuilder'.controller = ['CSS']
root.'griffon.builder.macwidgets.MacWidgetsBuilder'.view = '*'
root.'griffon.builder.jide.JideBuilder'.view = '*'