/*
 * Copyright 2008-2014 the original author or authors.
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
