import swing.*

features {
    factories = [
        "fuseDemo": [
            contentPanel: ContentPanel,
            headerPanel: HeaderPanel,
            footerPanel: FooterPanel,
            titleLabel: TitleLabel
        ]
    ]
    attributeDelegates = [
       { builder, node, attrs ->
          def injectResources = attrs.remove("injectResources")
          if( injectResources ) FuseInjector.addInjectTarget(node)
       }
    ]
}

root {
    'groovy.swing.SwingBuilder' {
        controller = ['Threading']
        view = "*"
    }
    'griffon.app.ApplicationBuilder' {
        view = "*"
    }
}
