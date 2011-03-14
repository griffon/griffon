/*
 * Copyright 2011 the original author or authors.
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
package griffon.util

import org.xml.sax.InputSource
import groovy.util.slurpersupport.GPathResult

/**
 * Translates an XML file into a Groovy script that is suitable for a Groovy builder.
 * String literals must be escaped either using single or double quotes.
 *
 * @author Andres Almiray
 */
@Singleton
class Xml2Groovy {
    String parse(File file) {
        translate(new XmlSlurper().parse(file))
    }
        
    String parse(InputSource source) {
        translate(new XmlSlurper().parse(source))
    }
        
    String parse(InputStream stream) {
        translate(new XmlSlurper().parse(stream))
    }
        
    String parse(Reader reader) {
        translate(new XmlSlurper().parse(reader))
    }
        
    String parse(String uri) {
        translate(new XmlSlurper().parse(uri))
    }
        
    String parse(GPathResult root) {
        translate(root)
    }
        
    String parseText(String text) {
        translate(new XmlSlurper().parseText(text))
    }
        
    private String translate(GPathResult root) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        IndentPrinter printer = createIndentPrinter(baos)
        walkXml(printer, root)
        printer.flush()
        
        baos.toString()
    }
    
    private IndentPrinter createIndentPrinter(OutputStream os) {
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(os))
        new IndentPrinter(pw)
    }
    
    private void walkXml(IndentPrinter printer, node) {
        printer.printIndent()
        printer.print(node.name())
        if(node.attributes()) {
            printer.print('(')
            String attrs = node.attributes().collect([]) { e ->
                "${e.key}: $e.value"
            }.join(', ')
            printer.print(attrs)
            printer.print(')')
        }

        if(node.children().size()) {
            printer.println(' {')
            printer.incrementIndent()
            for(child in node.children()) walkXml(printer, child)
            printer.decrementIndent()
            printer.printIndent()
            printer.println('}')
        } else if(node.attributes()) {
            printer.println('')
        } else {
            printer.println('()')
        }
    }
}
