/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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

import griffon.core.CallableWithArgs

class BindUtilsTest extends GroovyTestCase {
    void testAllOptions() {
        def source = new Object()
        def target = new Object()
        def property = 'property'
        CallableWithArgs<Object> converter = { arg -> arg }
        CallableWithArgs<Boolean> validator = { arg -> true }

        BindUtils.BindingBuilder bb = BindUtils.binding()
            .withSource(source)
            .withTarget(target)
            .withSourceProperty(property)
            .withTargetProperty(property)
            .withMutual(true)
            .withConverter(converter)
            .withValidator(validator)

        TestBuilder testBuilder = new TestBuilder()
        Factory factory = new WitnessFactory()
        testBuilder.registerFactory('bind', factory)

        bb.make(testBuilder)

        assert factory.attributes
        assert factory.attributes.source == source
        assert factory.attributes.target == target
        assert factory.attributes.sourceProperty == property
        assert factory.attributes.targetProperty == property
        assert factory.attributes.mutual
        assert factory.attributes.converter
        assert factory.attributes.validator
    }

    void testNoSourceProperty() {
        def source = new Object()
        def target = new Object()
        def property = 'property'

        BindUtils.BindingBuilder bb = BindUtils.binding()
            .withSource(source)
            .withTarget(target)
            .withTargetProperty(property)
            .withMutual(true)

        TestBuilder testBuilder = new TestBuilder()
        Factory factory = new WitnessFactory()
        testBuilder.registerFactory('bind', factory)

        bb.make(testBuilder)

        assert factory.attributes
        assert factory.attributes.source == source
        assert factory.attributes.target == target
        assert factory.attributes.sourceProperty == property
        assert factory.attributes.targetProperty == property
        assert factory.attributes.mutual
    }

    void testNoTargetProperty() {
        def source = new Object()
        def target = new Object()
        def property = 'property'

        BindUtils.BindingBuilder bb = BindUtils.binding()
            .withSource(source)
            .withTarget(target)
            .withSourceProperty(property)
            .withMutual(true)

        TestBuilder testBuilder = new TestBuilder()
        Factory factory = new WitnessFactory()
        testBuilder.registerFactory('bind', factory)

        bb.make(testBuilder)

        assert factory.attributes
        assert factory.attributes.source == source
        assert factory.attributes.target == target
        assert factory.attributes.sourceProperty == property
        assert factory.attributes.targetProperty == property
        assert factory.attributes.mutual
    }

    private static class TestBuilder extends FactoryBuilderSupport {

    }

    private static class WitnessFactory extends AbstractFactory {
        Map attributes = [:]

        @Override
        Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
            this.attributes.putAll(attributes)
            null
        }
    }
}
