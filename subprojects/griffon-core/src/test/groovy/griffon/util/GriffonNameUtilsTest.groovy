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

class GriffonNameUtilsTest extends GroovyTestCase {
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

    void testGetClassNameRepresentation() {
        assert "MyClass" == GriffonNameUtils.getClassNameRepresentation("my-class")
        assert "MyClass" == GriffonNameUtils.getClassNameRepresentation("MyClass")
        assert "F" == GriffonNameUtils.getClassNameRepresentation(".f")
        assert "AB" == GriffonNameUtils.getClassNameRepresentation(".a.b")
        assert "AlphaBakerCharlie" == GriffonNameUtils.getClassNameRepresentation(".alpha.baker.charlie")
    }

    void testGetNaturalName() {
        assert "First Name" == GriffonNameUtils.getNaturalName("firstName")
        assert "URL" == GriffonNameUtils.getNaturalName("URL")
        assert "Local URL" == GriffonNameUtils.getNaturalName("localURL")
        assert "URL local" == GriffonNameUtils.getNaturalName("URLlocal")
        assert "My Domain Class" == GriffonNameUtils.getNaturalName("MyDomainClass")
        assert "My Domain Class" == GriffonNameUtils.getNaturalName("com.myco.myapp.MyDomainClass")
    }

    void testGetLogicalName() {
        assert "Test" == GriffonNameUtils.getLogicalName("TestController", "Controller")
        assert "Test" == GriffonNameUtils.getLogicalName("org.music.TestController", "Controller")
    }

    void testGetLogicalPropertyName() {
        assert "myFunky" == GriffonNameUtils.getLogicalPropertyName("MyFunkyController", "Controller")
        assert "HTML" == GriffonNameUtils.getLogicalPropertyName("HTMLCodec", "Codec")
        assert "payRoll" == GriffonNameUtils.getLogicalPropertyName("org.something.PayRollController", "Controller")
    }

    void testGetLogicalPropertyNameForArtefactWithSingleCharacterName() {
        assert "a" == GriffonNameUtils.getLogicalPropertyName("AController", "Controller")
        assert "b" == GriffonNameUtils.getLogicalPropertyName("BService", "Service")
    }

    void testGetLogicalPropertyNameForArtefactWithAllUpperCaseName() {
        assert "ABC" == GriffonNameUtils.getLogicalPropertyName("ABCController", "Controller")
        assert "BCD" == GriffonNameUtils.getLogicalPropertyName("BCDService", "Service")
    }

    void testIsBlank() {
        assert GriffonNameUtils.isBlank(null), "'null' value should count as blank."
        assert GriffonNameUtils.isBlank(""), "Empty string should count as blank."
        assert GriffonNameUtils.isBlank("  "), "Spaces should count as blank."
        assert GriffonNameUtils.isBlank("\t"), "A tab should count as blank."
        assert !GriffonNameUtils.isBlank("\t  h"), "String with whitespace and non-whitespace should not count as blank."
        assert !GriffonNameUtils.isBlank("test"), "String should not count as blank."
    }

    void testIsNotBlank() {
        assert !GriffonNameUtils.isNotBlank(null), "'null' value should count as blank."
        assert !GriffonNameUtils.isNotBlank(""), "Empty string should count as blank."
        assert !GriffonNameUtils.isNotBlank("  "), "Spaces should count as blank."
        assert !GriffonNameUtils.isNotBlank("\t"), "A tab should count as blank."
        assert GriffonNameUtils.isNotBlank("\t  h"), "String with whitespace and non-whitespace should not count as blank."
        assert GriffonNameUtils.isNotBlank("test"), "String should not count as blank."
    }

    void testQuote() {
        assert " " == GriffonNameUtils.quote(" ")
        assert "\" a\"" == GriffonNameUtils.quote(" a")
        assert "\" a \"" == GriffonNameUtils.quote(" a ")
        assert "\"a \"" == GriffonNameUtils.quote("a ")
    }

    void testUnquote() {
        assert "" == GriffonNameUtils.unquote("")
        assert " " == GriffonNameUtils.unquote(" ")
        assert "" == GriffonNameUtils.unquote("\"\"")
        assert " " == GriffonNameUtils.unquote("\" \"")
        assert "foo" == GriffonNameUtils.unquote("\"foo\"")
        assert "" == GriffonNameUtils.unquote("''")
        assert " " == GriffonNameUtils.unquote("' '")
        assert "foo" == GriffonNameUtils.unquote("'foo'")
        assert "\"foo" == GriffonNameUtils.unquote("\"foo")
        assert "foo\"" == GriffonNameUtils.unquote("foo\"")
        assert "'foo" == GriffonNameUtils.unquote("'foo")
        assert "foo'" == GriffonNameUtils.unquote("foo'")
    }

    void testGetHyphenatedName() {
        assert "griffon-name-utils" == GriffonNameUtils.getHyphenatedName(GriffonNameUtils.class)
        assert "griffon-name-utils" == GriffonNameUtils.getHyphenatedName(GriffonNameUtils.class.getName())
    }

    void testGetClassNameForLowerCaseHyphenSeparatedName() {
        assert "GriffonNameUtils" == GriffonNameUtils.getClassNameForLowerCaseHyphenSeparatedName("griffon-name-utils")
    }
}

@SuppressWarnings('EmptyClass')
class Foo {}
