package griffon.plugins.jfxtras.factory

import griffon.util.GriffonClassUtils
import groovyx.javafx.factory.NodeFactory
import jfxtras.labs.scene.control.gauge.Gauge
import jfxtras.labs.scene.control.gauge.GaugeModel
import jfxtras.labs.scene.control.gauge.StyleModel

class GaugeFactory extends NodeFactory {
    final Class<? extends Gauge> gaugeClass

    GaugeFactory(Class<? extends Gauge> gaugeClass) {
        this.gaugeClass = gaugeClass
    }

    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) {
        value instanceof Gauge ? value : gaugeClass.newInstance()
    }

    @Override
    void setChild(FactoryBuilderSupport builder, Object parent, Object child) {
        switch (child) {
            case GaugeModel:
                parent.gaugeModel = child
                break
            case StyleModel:
                parent.styleModel = child
                break
            default:
                super.setChild(builder, parent, child)
        }
    }

    @Override
    boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map attributes) {
        handleSizeProperty(node, attributes, 'prefSize')
        handleSizeProperty(node, attributes, 'minSize')
        handleSizeProperty(node, attributes, 'maxSize')
        return super.onHandleNodeAttributes(builder, node, attributes)
    }

    private void handleSizeProperty(Object node, Map attributes, String propertyName) {
        def values = attributes.remove(propertyName)
        if (!values) return

        String methodName = GriffonClassUtils.getSetterName(propertyName)

        switch (values) {
            case Number:
                node."$methodName"(values.doubleValue(), values.doubleValue())
                break
            case List:
                node."$methodName"(values[0] as double, (values[1] != null ? values[1] : values[0]) as double)
                break
            default:
                throw new IllegalArgumentException("Don't know how to handle $values for ${propertyName}:")
        }
    }
}
