package griffon.util

class GriffonNameUtilsTests extends GroovyTestCase {
    void testCapitalize() {
        assert '' == GriffonNameUtils.capitalize('')
        assert ' ' == GriffonNameUtils.capitalize(' ')
        assert null == GriffonNameUtils.capitalize(null)
        assert 'A' == GriffonNameUtils.capitalize('a')
        assert 'Griffon' == GriffonNameUtils.capitalize('griffon')
    }

    void testUncapitalize() {
        assert '' == GriffonNameUtils.uncapitalize('')
        assert ' ' == GriffonNameUtils.uncapitalize(' ')
        assert null == GriffonNameUtils.uncapitalize(null)
        assert 'a' == GriffonNameUtils.uncapitalize('A')
        assert 'griffon' == GriffonNameUtils.uncapitalize('Griffon')
    }

    void testGetSetterName() {
        assert 'setA' == GriffonNameUtils.getSetterName('a')
        assert 'setGriffon' == GriffonNameUtils.getSetterName('griffon')
    }

    void testGetGetterName() {
        assert 'getA' == GriffonNameUtils.getGetterName('a')
        assert 'getGriffon' == GriffonNameUtils.getGetterName('griffon')
    }

    void testGetClassName() {
        shouldFail(IllegalArgumentException) {
            GriffonNameUtils.getClassName(null, 'Controller')
        }
        shouldFail(IllegalArgumentException) {
            GriffonNameUtils.getClassName('', 'Controller')
        }
        shouldFail(IllegalArgumentException) {
            GriffonNameUtils.getClassName(' ', 'Controller')
        }

        assert 'PersonController' == GriffonNameUtils.getClassName('person', 'Controller')
        assert 'Person' == GriffonNameUtils.getClassName('person', '')
    }

    void testGetPropertyName() {
        assert 'foo' == GriffonNameUtils.getPropertyName(Foo) 
    }

    void testGetPropertyNameRepresentation() {
        assert 'foo' == GriffonNameUtils.getPropertyName('foo') 
        assert 'foo' == GriffonNameUtils.getPropertyName('Foo') 
        assert 'foo' == GriffonNameUtils.getPropertyName('griffon.util.Foo') 
        assert 'fooBar' == GriffonNameUtils.getPropertyName('Foo Bar') 
    }

    void testGetShortName() {
        assert 'Foo' == GriffonNameUtils.getShortName('griffon.util.Foo')
        assert 'Foo' == GriffonNameUtils.getShortName(Foo)
    }
}

class Foo {}
