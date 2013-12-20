package org.codehaus.griffon.runtime.core.view

import griffon.core.view.BuilderCustomizer


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