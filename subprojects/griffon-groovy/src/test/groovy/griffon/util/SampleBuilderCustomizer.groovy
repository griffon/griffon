/*
 * Copyright 2008-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package griffon.util

import org.codehaus.griffon.runtime.groovy.view.AbstractBuilderCustomizer

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
