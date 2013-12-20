package griffon.util

import org.codehaus.griffon.runtime.core.view.AbstractBuilderCustomizer

class SampleBuilderCustomizer extends AbstractBuilderCustomizer {
    final String namespace

    SampleBuilderCustomizer(String namespace) {
        this.namespace = namespace

        setFactories([
            (namespace + 'Bean'): new AbstractFactory() {
                @Override
                Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
                    return new SampleBean()
                }
            }])

        setMethods(
            [(namespace + 'SomeMethod'): {
                delegate.variables[namespace + 'SomeMethodCalled'] = true
            }])

        setProps(
            [(namespace + 'SomeProperty'): [
                {-> delegate.variables.get(namespace + 'SomeProperty') ?: 'someValue' },
                { value -> delegate.variables.put(namespace + 'SomeProperty', value) }
            ] as Closure[]])

        setAttributeDelegates([{ builder, node, attrs -> builder.variables[namespace + 'AttributeDelegate'] = true }])
        setPreInstantiateDelegates([{ builder, attrs, value -> builder.variables[namespace + 'PreInstantiateDelegate'] = true }])
        setPostInstantiateDelegates([{ builder, attrs, node -> builder.variables[namespace + 'PostInstantiateDelegate'] = true }])
        setPostNodeCompletionDelegates([{ builder, parent, node -> builder.variables[namespace + 'PostNodeCompletionDelegate'] = true }])
    }
}
