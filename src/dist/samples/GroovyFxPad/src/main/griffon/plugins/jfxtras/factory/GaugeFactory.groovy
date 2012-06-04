/*
 * Copyright 2007-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 */
package griffon.plugins.jfxtras.factory

import griffon.util.GriffonClassUtils
import groovyx.javafx.factory.NodeFactory
import jfxtras.labs.scene.control.gauge.Gauge
import jfxtras.labs.scene.control.gauge.GaugeModel
import jfxtras.labs.scene.control.gauge.StyleModel

/**
 * @author Andres Almiray
 */
class GaugeFactory extends NodeFactory {
    final Class<? extends Gauge> gaugeClass

    GaugeFactory(Class<? extends Gauge> gaugeClass) {
        super(gaugeClass)
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
