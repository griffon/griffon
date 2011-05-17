/* 
 * Copyright 2004-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package griffon.util;

import groovy.util.XmlSlurper;
import groovy.util.slurpersupport.GPathResult;
import junit.framework.TestCase;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Tests for the GriffonUtils class
 *
 * @author Graeme Rocher (Grails 0.4)
 */
public class GriffonUtilTests extends TestCase {
//    public void testGriffonVersion() {
//        assertEquals("0.9-SNAPSHOT", GriffonUtil.getGriffonVersion());
//    }

    protected void tearDown() throws Exception {
        System.setProperty(Environment.KEY, "");
    }

    @SuppressWarnings("deprecation")
    public void testWriteSlurperResult() throws SAXException, ParserConfigurationException, IOException {
        String testXml = "<root><books><book isbn=\"45734957\">" +
                "<title>Misery</title><author>Stephen King</author>" +
                "</book></books></root>";
        GPathResult result = new XmlSlurper().parseText(testXml);

        StringWriter output = new StringWriter(testXml.length() + 20);
        GriffonUtil.writeSlurperResult(result, output);

        testXml = testXml.replaceAll("<root>", "<root xmlns='http://java.sun.com/xml/ns/j2ee'>");
        testXml = testXml.replace('"', '\'');
        assertEquals(testXml, output.toString());
    }


    public void testGetClassNameRepresentation() {
        assertEquals("MyClass", GriffonUtil.getClassNameRepresentation("my-class"));
        assertEquals("MyClass", GriffonUtil.getClassNameRepresentation("MyClass"));
    }

    public void testGetNaturalName() {
        assertEquals("First Name", GriffonUtil.getNaturalName("firstName"));
        assertEquals("URL", GriffonUtil.getNaturalName("URL"));
        assertEquals("Local URL", GriffonUtil.getNaturalName("localURL"));
        assertEquals("URL local", GriffonUtil.getNaturalName("URLlocal"));
        assertEquals("My Domain Class", GriffonUtil.getNaturalName("MyDomainClass"));
        assertEquals("My Domain Class", GriffonUtil.getNaturalName("com.myco.myapp.MyDomainClass"));
    }

    public void testGetLogicalName() {
        assertEquals("Test", GriffonUtil.getLogicalName("TestController", "Controller"));
        assertEquals("Test", GriffonUtil.getLogicalName("org.music.TestController", "Controller"));
    }

    public void testGetLogicalPropertyName() {
        assertEquals("myFunky", GriffonUtil.getLogicalPropertyName("MyFunkyController", "Controller"));
        assertEquals("HTML", GriffonUtil.getLogicalPropertyName("HTMLCodec", "Codec"));
        assertEquals("payRoll", GriffonUtil.getLogicalPropertyName("org.something.PayRollController", "Controller"));
    }

    public void testGetLogicalPropertyNameForArtefactWithSingleCharacterName() {
        assertEquals("a", GriffonUtil.getLogicalPropertyName("AController", "Controller"));
        assertEquals("b", GriffonUtil.getLogicalPropertyName("BService", "Service"));
    }
    
    public void testGetLogicalPropertyNameForArtefactWithAllUpperCaseName() {
        assertEquals("ABC", GriffonUtil.getLogicalPropertyName("ABCController", "Controller"));
        assertEquals("BCD", GriffonUtil.getLogicalPropertyName("BCDService", "Service"));
    }
    
    public void testGetScriptName() {
        assertEquals("griffon-util-tests", GriffonUtil.getScriptName(getClass()));
        assertEquals("", GriffonUtil.getScriptName(""));
        assertNull(GriffonUtil.getScriptName((String) null));
        assertNull(GriffonUtil.getScriptName((Class<?>) null));
    }

    public void testGetNameFromScript() {
        assertEquals("GriffonClassUtilsTests", GriffonUtil.getNameFromScript("griffon-class-utils-tests"));
        assertEquals("Griffon", GriffonUtil.getNameFromScript("griffon"));
        assertEquals("CreateApp", GriffonUtil.getNameFromScript("create-app"));
        assertEquals("", GriffonUtil.getNameFromScript(""));
        assertNull(GriffonUtil.getNameFromScript(null));
    }

    public void testGetPluginName() {
        assertEquals("db-utils", GriffonUtil.getPluginName("DbUtilsGriffonPlugin.groovy"));
        assertEquals("shiro", GriffonUtil.getPluginName("ShiroGriffonPlugin.groovy"));
        // The following isn't supported yet - but it should be.
//        assertEquals("CAS-security", GriffonUtil.getPluginName("CASSecurityGriffonPlugin.groovy"));
        assertEquals("", GriffonUtil.getPluginName(""));
        assertNull(GriffonUtil.getPluginName(null));

        try {
            GriffonUtil.getPluginName("NotAPlugin.groovy");
            fail("GriffonUtil.getPluginName() should have thrown an IllegalArgumentException.");
        }
        catch (IllegalArgumentException ex) {
            // Expected!
        }
    }

    public void testIsBlank() {
        assertTrue("'null' value should count as blank.", GriffonUtil.isBlank(null));
        assertTrue("Empty string should count as blank.", GriffonUtil.isBlank(""));
        assertTrue("Spaces should count as blank.", GriffonUtil.isBlank("  "));
        assertTrue("A tab should count as blank.", GriffonUtil.isBlank("\t"));
        assertFalse("String with whitespace and non-whitespace should not count as blank.", GriffonUtil.isBlank("\t  h"));
        assertFalse("String should not count as blank.", GriffonUtil.isBlank("test"));
    }
}
