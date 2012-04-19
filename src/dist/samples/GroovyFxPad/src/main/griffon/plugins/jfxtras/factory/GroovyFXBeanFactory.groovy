package griffon.plugins.jfxtras.factory

import groovyx.javafx.factory.AbstractGroovyFXFactory

class GroovyFXBeanFactory extends AbstractGroovyFXFactory {
    final Class beanClass
    final boolean leaf

    GroovyFXBeanFactory(Class beanClass) {
        this(beanClass, false)
    }

    GroovyFXBeanFactory(Class beanClass, boolean leaf) {
        this.beanClass = beanClass
        this.leaf = leaf
    }

    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) {
        if (value instanceof CharSequence) value = value.toString()
        if (FactoryBuilderSupport.checkValueIsTypeNotString(value, name, beanClass)) {
            return value
        }
        return beanClass.newInstance()
    }
}
