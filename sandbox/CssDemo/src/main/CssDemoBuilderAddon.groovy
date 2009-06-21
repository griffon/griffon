import com.u2d.css4swing.style.ComponentStyle

class CssDemoBuilderAddon {

    def attributeDelegates = [
        {builder, node, attrs ->
            def cssClass = attrs.remove('cssClass')
            if (cssClass) ComponentStyle.addClass(node, cssClass)
        }
    ]

    def mvcGroups = [
        // MVC Group for "NothingPanel"
        NothingPanel: [
            model: 'group.NothingPanelModel',
            view: 'group.NothingPanelView',
            controller: 'group.NothingPanelController'
        ]
    ]


    def addonInit(app) {
        println "init the $app!"
    }

    def addonPostInit = {app ->
        println "postInit, as a closure!"
    }

    def addonBuilderInit(app, builder) {
        println "init the $builder in $app!"
    }

    def addonBuilderPostInit = {app, builder ->
        println "builder postInit, as a closure!"
    }
}
