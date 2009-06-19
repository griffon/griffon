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

}
