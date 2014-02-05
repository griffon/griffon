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
package griffon.util;

import junit.framework.TestCase;

public class GriffonNameUtilsTest extends TestCase {
    public void testGetClassNameRepresentation() {
        assertEquals("MyClass", GriffonNameUtils.getClassNameRepresentation("my-class"));
        assertEquals("MyClass", GriffonNameUtils.getClassNameRepresentation("MyClass"));
        assertEquals("F", GriffonNameUtils.getClassNameRepresentation(".f"));
        assertEquals("AB", GriffonNameUtils.getClassNameRepresentation(".a.b"));
        assertEquals("AlphaBakerCharlie", GriffonNameUtils.getClassNameRepresentation(".alpha.baker.charlie"));
    }

    public void testGetNaturalName() {
        assertEquals("First Name", GriffonNameUtils.getNaturalName("firstName"));
        assertEquals("URL", GriffonNameUtils.getNaturalName("URL"));
        assertEquals("Local URL", GriffonNameUtils.getNaturalName("localURL"));
        assertEquals("URL local", GriffonNameUtils.getNaturalName("URLlocal"));
        assertEquals("My Domain Class", GriffonNameUtils.getNaturalName("MyDomainClass"));
        assertEquals("My Domain Class", GriffonNameUtils.getNaturalName("com.myco.myapp.MyDomainClass"));
    }

    public void testGetLogicalName() {
        assertEquals("Test", GriffonNameUtils.getLogicalName("TestController", "Controller"));
        assertEquals("Test", GriffonNameUtils.getLogicalName("org.music.TestController", "Controller"));
    }

    public void testGetLogicalPropertyName() {
        assertEquals("myFunky", GriffonNameUtils.getLogicalPropertyName("MyFunkyController", "Controller"));
        assertEquals("HTML", GriffonNameUtils.getLogicalPropertyName("HTMLCodec", "Codec"));
        assertEquals("payRoll", GriffonNameUtils.getLogicalPropertyName("org.something.PayRollController", "Controller"));
    }

    public void testGetLogicalPropertyNameForArtefactWithSingleCharacterName() {
        assertEquals("a", GriffonNameUtils.getLogicalPropertyName("AController", "Controller"));
        assertEquals("b", GriffonNameUtils.getLogicalPropertyName("BService", "Service"));
    }

    public void testGetLogicalPropertyNameForArtefactWithAllUpperCaseName() {
        assertEquals("ABC", GriffonNameUtils.getLogicalPropertyName("ABCController", "Controller"));
        assertEquals("BCD", GriffonNameUtils.getLogicalPropertyName("BCDService", "Service"));
    }

    public void testIsBlank() {
        assertTrue("'null' value should count as blank.", GriffonNameUtils.isBlank(null));
        assertTrue("Empty string should count as blank.", GriffonNameUtils.isBlank(""));
        assertTrue("Spaces should count as blank.", GriffonNameUtils.isBlank("  "));
        assertTrue("A tab should count as blank.", GriffonNameUtils.isBlank("\t"));
        assertFalse("String with whitespace and non-whitespace should not count as blank.", GriffonNameUtils.isBlank("\t  h"));
        assertFalse("String should not count as blank.", GriffonNameUtils.isBlank("test"));
    }

    public void testQuote() {
        assertEquals(" ", GriffonNameUtils.quote(" "));
        assertEquals("\" a\"", GriffonNameUtils.quote(" a"));
        assertEquals("\" a \"", GriffonNameUtils.quote(" a "));
        assertEquals("\"a \"", GriffonNameUtils.quote("a "));
    }

    public void testUnquote() {
        assertEquals("", GriffonNameUtils.unquote(""));
        assertEquals(" ", GriffonNameUtils.unquote(" "));
        assertEquals("", GriffonNameUtils.unquote("\"\""));
        assertEquals(" ", GriffonNameUtils.unquote("\" \""));
        assertEquals("foo", GriffonNameUtils.unquote("\"foo\""));
        assertEquals("", GriffonNameUtils.unquote("''"));
        assertEquals(" ", GriffonNameUtils.unquote("' '"));
        assertEquals("foo", GriffonNameUtils.unquote("'foo'"));
        assertEquals("\"foo", GriffonNameUtils.unquote("\"foo"));
        assertEquals("foo\"", GriffonNameUtils.unquote("foo\""));
        assertEquals("'foo", GriffonNameUtils.unquote("'foo"));
        assertEquals("foo'", GriffonNameUtils.unquote("foo'"));
    }

    public void testGetHyphenatedName() {
        assertEquals("griffon-name-utils", GriffonNameUtils.getHyphenatedName(GriffonNameUtils.class));
        assertEquals("griffon-name-utils", GriffonNameUtils.getHyphenatedName(GriffonNameUtils.class.getName()));
    }

    public void testGetClassNameForLowerCaseHyphenSeparatedName() {
        assertEquals("GriffonNameUtils", GriffonNameUtils.getClassNameForLowerCaseHyphenSeparatedName("griffon-name-utils"));
    }
}
