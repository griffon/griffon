/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2020 the original author or authors.
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
package griffon.util.groovy

class CompositeBuilderTest extends GroovyTestCase {
    void testWithTwoBuilderCustomizers() {
        SampleBuilderCustomizer one = new SampleBuilderCustomizer('one')
        SampleBuilderCustomizer two = new SampleBuilderCustomizer('two')

        CompositeBuilder compositeBuilder = new CompositeBuilder([one, two] as BuilderCustomizer[])

        assert !compositeBuilder.getVariables()['oneAttributeDelegate']
        assert !compositeBuilder.getVariables()['onePreInstantiateDelegate']
        assert !compositeBuilder.getVariables()['onePostInstantiateDelegate']
        assert !compositeBuilder.getVariables()['onePostNodeCompletionDelegate']
        assert !compositeBuilder.getVariables()['twoAttributeDelegate']
        assert !compositeBuilder.getVariables()['twoPreInstantiateDelegate']
        assert !compositeBuilder.getVariables()['twoPostInstantiateDelegate']
        assert !compositeBuilder.getVariables()['twoPostNodeCompletionDelegate']

        def bean = compositeBuilder.oneBean(name: 'Griffon')
        assert bean
        assert bean.name == 'Griffon'

        bean = compositeBuilder.twoBean(name: 'Griffon')
        assert bean
        assert bean.name == 'Griffon'

        assert !compositeBuilder.getVariables()['oneSomeMethodCalled']
        compositeBuilder.oneSomeMethod()
        assert compositeBuilder.getVariables()['oneSomeMethodCalled']

        assert !compositeBuilder.getVariables()['oneSomeProperty']
        assert compositeBuilder.oneSomeProperty == 'someValue'
        compositeBuilder.oneSomeProperty = 'someOtherValue'
        assert compositeBuilder.oneSomeProperty == 'someOtherValue'

        assert !compositeBuilder.getVariables()['twoSomeMethodCalled']
        compositeBuilder.twoSomeMethod()
        assert compositeBuilder.getVariables()['twoSomeMethodCalled']

        assert !compositeBuilder.getVariables()['twoSomeProperty']
        assert compositeBuilder.twoSomeProperty == 'someValue'
        compositeBuilder.twoSomeProperty = 'someOtherValue'
        assert compositeBuilder.twoSomeProperty == 'someOtherValue'

        assert compositeBuilder.getVariables()['oneAttributeDelegate']
        assert compositeBuilder.getVariables()['onePreInstantiateDelegate']
        assert compositeBuilder.getVariables()['onePostInstantiateDelegate']
        assert compositeBuilder.getVariables()['onePostNodeCompletionDelegate']
        assert compositeBuilder.getVariables()['twoAttributeDelegate']
        assert compositeBuilder.getVariables()['twoPreInstantiateDelegate']
        assert compositeBuilder.getVariables()['twoPostInstantiateDelegate']
        assert compositeBuilder.getVariables()['twoPostNodeCompletionDelegate']

        shouldFail(MissingMethodException) { compositeBuilder.methodDoesNotExist() }
        shouldFail(MissingMethodException) { compositeBuilder.someMethod() }
        shouldFail(MissingPropertyException) { compositeBuilder.propertyDoesNotExist }
        shouldFail(MissingPropertyException) { compositeBuilder.someProperty }
    }

    void testMethodMissingDelegate() {
        SampleBuilderCustomizer one = new SampleBuilderCustomizer('one')
        SampleBuilderCustomizer two = new SampleBuilderCustomizer('two')

        CompositeBuilder compositeBuilder = new CompositeBuilder([one, two] as BuilderCustomizer[])
        compositeBuilder.setMethodMissingDelegate { String methodName, args ->
            compositeBuilder['methodCalled'] = methodName
        }

        assert !compositeBuilder.getVariables()['methodCalled']
        compositeBuilder.randomMethod()
        assert compositeBuilder.getVariables()['methodCalled'] == 'randomMethod'

        compositeBuilder.setMethodMissingDelegate(null)

        shouldFail(MissingMethodException) {
            compositeBuilder.someOtherMethod()
        }
    }

    void testMethodMissingDelegate_noDelegate() {
        SampleBuilderCustomizer one = new SampleBuilderCustomizer('one')
        SampleBuilderCustomizer two = new SampleBuilderCustomizer('two')

        CompositeBuilder compositeBuilder = new CompositeBuilder([one, two] as BuilderCustomizer[])

        assert !compositeBuilder.getVariables()['methodCalled']
        shouldFail(MissingMethodException) {
            compositeBuilder.randomMethod()
        }
        assert !compositeBuilder.getVariables()['methodCalled']
    }

    void testMethodMissingDelegate_handledByCustomizer() {
        String methodCalled = null
        SampleBuilderCustomizer one = new SampleBuilderCustomizer('one') {
            {
                setMethodMissingDelegate { String methodName, args ->
                    methodCalled = methodName
                }
            }
        }
        SampleBuilderCustomizer two = new SampleBuilderCustomizer('two')

        CompositeBuilder compositeBuilder = new CompositeBuilder([one, two] as BuilderCustomizer[])

        assert !methodCalled
        compositeBuilder.randomMethod()
        assert methodCalled == 'randomMethod'

        compositeBuilder.setMethodMissingDelegate(null)

        shouldFail(MissingMethodException) {
            compositeBuilder.someOtherMethod()
        }
    }

    void testMethodMissingDelegate_unhandledByCustomizer() {
        SampleBuilderCustomizer one = new SampleBuilderCustomizer('one') {
            {
                setMethodMissingDelegate { String methodName, args ->
                    throw new MissingMethodException(methodName, CompositeBuilder, args)
                }
            }
        }
        SampleBuilderCustomizer two = new SampleBuilderCustomizer('two')

        CompositeBuilder compositeBuilder = new CompositeBuilder([one, two] as BuilderCustomizer[])
        compositeBuilder.setMethodMissingDelegate { String methodName, args ->
            compositeBuilder['methodCalled'] = methodName
        }

        assert !compositeBuilder.getVariables()['methodCalled']
        compositeBuilder.randomMethod()
        assert compositeBuilder.getVariables()['methodCalled'] == 'randomMethod'

        compositeBuilder.setMethodMissingDelegate(null)

        shouldFail(MissingMethodException) {
            compositeBuilder.someOtherMethod()
        }
    }

    void testMethodMissingDelegate_abortedByCustomizer() {
        SampleBuilderCustomizer one = new SampleBuilderCustomizer('one') {
            {
                setMethodMissingDelegate { String methodName, args ->
                    throw new MissingMethodException('boom', CompositeBuilder, args)
                }
            }
        }
        SampleBuilderCustomizer two = new SampleBuilderCustomizer('two')

        CompositeBuilder compositeBuilder = new CompositeBuilder([one, two] as BuilderCustomizer[])

        assert !compositeBuilder.getVariables()['methodCalled']
        shouldFail(MissingMethodException) {
            compositeBuilder.randomMethod()
        }
        assert !compositeBuilder.getVariables()['methodCalled']
    }

    void testPropertyMissingDelegate() {
        SampleBuilderCustomizer one = new SampleBuilderCustomizer('one')
        SampleBuilderCustomizer two = new SampleBuilderCustomizer('two')

        CompositeBuilder compositeBuilder = new CompositeBuilder([one, two] as BuilderCustomizer[])
        compositeBuilder.setPropertyMissingDelegate { String propertyName, Object... args ->
            compositeBuilder.variables['propertyAccessed'] = propertyName
            !args ? null : args
        }

        assert !compositeBuilder.getVariables()['propertyAccessed']
        assert !compositeBuilder.getVariable('randomProperty')
        compositeBuilder.setVariable('randomProperty', 'randomValue')
        assert compositeBuilder.getVariables()['propertyAccessed'] == 'randomProperty'
        assert compositeBuilder.randomProperty == 'randomValue'

        compositeBuilder.setPropertyMissingDelegate(null)

        shouldFail(MissingPropertyException) {
            assert !compositeBuilder.someOtherProperty
        }
    }
}