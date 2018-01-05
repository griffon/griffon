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
package griffon.util;

import groovy.util.IndentPrinter;
import groovy.util.XmlSlurper;
import groovy.util.slurpersupport.GPathResult;
import groovy.util.slurpersupport.Node;
import groovy.util.slurpersupport.NodeChild;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Translates an XML file into a Groovy script that is suitable for a Groovy builder.
 * String literals must be escaped either using single or double quotes. <p>
 * This helper class is useful for translating an XML View definition into a Groovy
 * script that can be handled by an UberBuilder, for example this View
 *
 * <xmp>
    <application title="app.config.application.title"
                 pack="true">
        <actions>
            <action id="'clickAction'"
                    name="'Click'"
                    closure="{controller.click(it)}"/>
        </actions>
    
        <gridLayout cols="1" rows="3"/>
        <textField id="'input'" columns="20"
            text="bind('value', target: model)"/>
        <textField id="'output'" columns="20"
            text="bind{model.value}" editable="false"/>
        <button action="clickAction"/>
    </application>
 * </xmp>
 *
 * results in the following script
 *
 * <pre>
application(title: app.config.application.title, pack: true) {
  actions {
    action(id: 'clickAction', name: 'Click', closure: {controller.click(it)})
  }
  gridLayout(cols: 1, rows: 3)
  textField(id: 'input', text: bind('value', target: model), columns: 20)
  textField(id: 'output', text: bind{model.value}, columns: 20, editable: false)
  button(action: clickAction)
}
 * </pre>
 *
 * @author Andres Almiray
 */
public final class Xml2Groovy {
    private static final Xml2Groovy INSTANCE;

    static {
        INSTANCE = new Xml2Groovy();
    }

    public static Xml2Groovy getInstance() {
        return INSTANCE;
    }

    private Xml2Groovy() {

    }

    public String parse(File file) {
        try {
            return translate(newXmlSlurper().parse(file));
        } catch (IOException | SAXException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String parse(InputSource source) {
        try {
            return translate(newXmlSlurper().parse(source));
        } catch (IOException | SAXException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String parse(InputStream stream) {
        try {
            return translate(newXmlSlurper().parse(stream));
        } catch (IOException | SAXException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String parse(Reader reader) {
        try {
            return translate(newXmlSlurper().parse(reader));
        } catch (IOException | SAXException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String parse(String uri) {
        try {
            return translate(newXmlSlurper().parse(uri));
        } catch (IOException | SAXException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String parse(GPathResult root) {
        return translate(root);
    }

    public String parseText(String text) {
        try {
            return translate(newXmlSlurper().parseText(text));
        } catch (IOException | SAXException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private XmlSlurper newXmlSlurper() {
        try {
            return new XmlSlurper();
        } catch (ParserConfigurationException | SAXException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private String translate(GPathResult root) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IndentPrinter printer = createIndentPrinter(baos);
        walkXml(printer, (NodeChild) root);
        printer.flush();

        return baos.toString();
    }

    private IndentPrinter createIndentPrinter(OutputStream os) {
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(os));
        return new IndentPrinter(pw, "    ");
    }

    private void walkXml(IndentPrinter printer, NodeChild node) {
        printer.printIndent();
        printer.print(node.name());
        if (!node.attributes().isEmpty()) {
            printer.print("(");
            List<String> attrs = new ArrayList<>();
            for (Object o : node.attributes().entrySet()) {
                Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
                attrs.add(entry.getKey() + ": " + entry.getValue());
            }
            printer.print(DefaultGroovyMethods.join((Iterable) attrs, ", "));
            printer.print(")");
        }

        if (node.children().size() > 0) {
            printer.println(" {");
            printer.incrementIndent();
            for (Iterator<?> iter = node.childNodes(); iter.hasNext(); ) {
                Object child = iter.next();
                if (child instanceof NodeChild) {
                    walkXml(printer, (NodeChild) child);
                } else if (child instanceof Node) {
                    walkXml(printer, (Node) child);
                }
            }
            printer.decrementIndent();
            printer.printIndent();
            printer.println("}");
        } else if (!node.attributes().isEmpty()) {
            printer.println("");
        } else {
            printer.println("()");
        }
    }

    private void walkXml(IndentPrinter printer, Node node) {
        printer.printIndent();
        printer.print(node.name());
        if (!node.attributes().isEmpty()) {
            printer.print("(");
            List<String> attrs = new ArrayList<>();
            for (Object o : node.attributes().entrySet()) {
                Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
                attrs.add(entry.getKey() + ": " + entry.getValue());
            }
            printer.print(DefaultGroovyMethods.join((Iterable) attrs, ", "));
            printer.print(")");
        }

        if (!node.children().isEmpty()) {
            printer.println(" {");
            printer.incrementIndent();
            for (Iterator<?> iter = node.childNodes(); iter.hasNext(); ) {
                Object child = iter.next();
                if (child instanceof NodeChild) {
                    walkXml(printer, (NodeChild) child);
                } else if (child instanceof Node) {
                    walkXml(printer, (Node) child);
                }
            }
            printer.decrementIndent();
            printer.printIndent();
            printer.println("}");
        } else if (!node.attributes().isEmpty()) {
            printer.println("");
        } else {
            printer.println("()");
        }
    }
}
