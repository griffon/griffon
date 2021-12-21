/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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

class StringUtilsTest extends GroovyTestCase {
    void testCapitalize() {
        assert '' == StringUtils.capitalize('')
        assert ' ' == StringUtils.capitalize(' ')
        assert null == StringUtils.capitalize(null)
        assert 'A' == StringUtils.capitalize('a')
        assert 'Griffon' == StringUtils.capitalize('griffon')
    }

    void testUncapitalize() {
        assert '' == StringUtils.uncapitalize('')
        assert ' ' == StringUtils.uncapitalize(' ')
        assert null == StringUtils.uncapitalize(null)
        assert 'a' == StringUtils.uncapitalize('A')
        assert 'griffon' == StringUtils.uncapitalize('Griffon')
    }

    void testGetSetterName() {
        assert 'setA' == StringUtils.getSetterName('a')
        assert 'setGriffon' == StringUtils.getSetterName('griffon')
    }

    void testGetGetterName() {
        assert 'getA' == StringUtils.getGetterName('a')
        assert 'getGriffon' == StringUtils.getGetterName('griffon')
    }

    void testGetClassName() {
        shouldFail(IllegalArgumentException) {
            StringUtils.getClassName(null, 'Controller')
        }
        shouldFail(IllegalArgumentException) {
            StringUtils.getClassName('', 'Controller')
        }
        shouldFail(IllegalArgumentException) {
            StringUtils.getClassName(' ', 'Controller')
        }

        assert 'PersonController' == StringUtils.getClassName('person', 'Controller')
        assert 'Person' == StringUtils.getClassName('person', '')
    }

    void testGetPropertyName() {
        assert 'foo' == StringUtils.getPropertyName(Foo)
    }

    void testGetPropertyNameRepresentation() {
        assert 'foo' == StringUtils.getPropertyName('foo')
        assert 'foo' == StringUtils.getPropertyName('Foo')
        assert 'foo' == StringUtils.getPropertyName('griffon.util.Foo')
        assert 'fooBar' == StringUtils.getPropertyName('Foo Bar')
    }

    void testGetShortName() {
        assert 'Foo' == StringUtils.getShortName('griffon.util.Foo')
        assert 'Foo' == StringUtils.getShortName(Foo)
    }

    void testGetClassNameRepresentation() {
        assert "MyClass" == StringUtils.getClassNameRepresentation("my-class")
        assert "MyClass" == StringUtils.getClassNameRepresentation("MyClass")
        assert "F" == StringUtils.getClassNameRepresentation(".f")
        assert "AB" == StringUtils.getClassNameRepresentation(".a.b")
        assert "AlphaBakerCharlie" == StringUtils.getClassNameRepresentation(".alpha.baker.charlie")
    }

    void testGetNaturalName() {
        assert "First Name" == StringUtils.getNaturalName("firstName")
        assert "URL" == StringUtils.getNaturalName("URL")
        assert "Local URL" == StringUtils.getNaturalName("localURL")
        assert "URL local" == StringUtils.getNaturalName("URLlocal")
        assert "My Domain Class" == StringUtils.getNaturalName("MyDomainClass")
        assert "My Domain Class" == StringUtils.getNaturalName("com.myco.myapp.MyDomainClass")
    }

    void testGetLogicalName() {
        assert "Test" == StringUtils.getLogicalName("TestController", "Controller")
        assert "Test" == StringUtils.getLogicalName("org.music.TestController", "Controller")
    }

    void testGetLogicalPropertyName() {
        assert "myFunky" == StringUtils.getLogicalPropertyName("MyFunkyController", "Controller")
        assert "HTML" == StringUtils.getLogicalPropertyName("HTMLCodec", "Codec")
        assert "payRoll" == StringUtils.getLogicalPropertyName("org.something.PayRollController", "Controller")
    }

    void testGetLogicalPropertyNameForArtefactWithSingleCharacterName() {
        assert "a" == StringUtils.getLogicalPropertyName("AController", "Controller")
        assert "b" == StringUtils.getLogicalPropertyName("BService", "Service")
    }

    void testGetLogicalPropertyNameForArtefactWithAllUpperCaseName() {
        assert "ABC" == StringUtils.getLogicalPropertyName("ABCController", "Controller")
        assert "BCD" == StringUtils.getLogicalPropertyName("BCDService", "Service")
    }

    void testIsBlank() {
        assert StringUtils.isBlank(null), "'null' value should count as blank."
        assert StringUtils.isBlank(""), "Empty string should count as blank."
        assert StringUtils.isBlank("  "), "Spaces should count as blank."
        assert StringUtils.isBlank("\t"), "A tab should count as blank."
        assert !StringUtils.isBlank("\t  h"), "String with whitespace and non-whitespace should not count as blank."
        assert !StringUtils.isBlank("test"), "String should not count as blank."
    }

    void testIsNotBlank() {
        assert !StringUtils.isNotBlank(null), "'null' value should count as blank."
        assert !StringUtils.isNotBlank(""), "Empty string should count as blank."
        assert !StringUtils.isNotBlank("  "), "Spaces should count as blank."
        assert !StringUtils.isNotBlank("\t"), "A tab should count as blank."
        assert StringUtils.isNotBlank("\t  h"), "String with whitespace and non-whitespace should not count as blank."
        assert StringUtils.isNotBlank("test"), "String should not count as blank."
    }

    void testQuote() {
        assert " " == StringUtils.quote(" ")
        assert "\" a\"" == StringUtils.quote(" a")
        assert "\" a \"" == StringUtils.quote(" a ")
        assert "\"a \"" == StringUtils.quote("a ")
    }

    void testUnquote() {
        assert "" == StringUtils.unquote("")
        assert " " == StringUtils.unquote(" ")
        assert "" == StringUtils.unquote("\"\"")
        assert " " == StringUtils.unquote("\" \"")
        assert "foo" == StringUtils.unquote("\"foo\"")
        assert "" == StringUtils.unquote("''")
        assert " " == StringUtils.unquote("' '")
        assert "foo" == StringUtils.unquote("'foo'")
        assert "\"foo" == StringUtils.unquote("\"foo")
        assert "foo\"" == StringUtils.unquote("foo\"")
        assert "'foo" == StringUtils.unquote("'foo")
        assert "foo'" == StringUtils.unquote("foo'")
    }

    void testGetHyphenatedName() {
        assert "string-utils" == StringUtils.getHyphenatedName(StringUtils.class)
        assert "string-utils" == StringUtils.getHyphenatedName(StringUtils.class.getName())
    }

    void testGetClassNameForLowerCaseHyphenSeparatedName() {
        assert "StringUtils" == StringUtils.getClassNameForLowerCaseHyphenSeparatedName("string-utils")
    }
}

@SuppressWarnings('EmptyClass')
class Foo {}
