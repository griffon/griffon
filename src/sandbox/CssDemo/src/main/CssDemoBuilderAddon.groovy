import com.u2d.css4swing.style.ComponentStyle
import groovy.swing.factory.BeanFactory
import javax.swing.JLabel

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

    def methods = [
        nothingMethod: { println "nothing Method" }
    ]

    def props = [
        nothingProp: [ get:{"Nothing in this Property!"} ]
    ]

    def factories = [
        nothingWidget : new BeanFactory(JLabel)
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
