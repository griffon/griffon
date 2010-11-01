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
        controller = ['withWorker']
        view = '*'
    }
}

root.'griffon.builder.jide.JideBuilder'.view = '*'
root.'griffon.builder.css.CSSBuilder'.view = '*'
root.'griffon.builder.trident.TridentBuilder'.view = '*'
root.'griffon.builder.gfx.GfxBuilder'.view = '*'
root.'CoverflowGriffonAddon'.addon=true
root.'JtreemapGriffonAddon'.addon=true
root.'JungGriffonAddon'.addon=true
root.'FlyingsaucerGriffonAddon'.addon=true
root.'GlazedlistsGriffonAddon'.addon=true
root.'JBusyComponentGriffonAddon'.addon=true
root.'griffon.builder.swingxtras.SwingxtrasBuilder'.view = '*'
root.'JexploseGriffonAddon'.addon=true
root.'DockingFrameGriffonAddon'.addon=true
root.'JxlayerGriffonAddon'.addon=true
root.'DesigngridlayoutGriffonAddon'.addon=true
root.'EffectsGriffonAddon'.addon=true
root.'MiglayoutGriffonAddon'.addon=true
root.'RiverlayoutGriffonAddon'.addon=true
root.'OxbowGriffonAddon'.addon=true
root.'OxbowGriffonAddon'.controller=['ask','choice','error','inform','showException','radioChoice','warn']
root.'TransitionsGriffonAddon'.addon=true
root.'ZonelayoutGriffonAddon'.addon=true
root.'TrayBuilderGriffonAddon'.addon=true
root.'griffon.builder.macwidgets.MacWidgetsBuilder'.view = '*'
root.'JGoodiesFormsGriffonAddon'.addon=true
root.'CrystaliconsGriffonAddon'.addon=true
