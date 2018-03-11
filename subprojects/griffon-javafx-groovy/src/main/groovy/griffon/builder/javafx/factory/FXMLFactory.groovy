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
package griffon.builder.javafx.factory

import griffon.javafx.support.JavaFXUtils
import groovyx.javafx.factory.AbstractNodeFactory
import javafx.fxml.FXMLLoader
import javafx.scene.Group
import javafx.scene.Node

/**
 * @author jimclarke
 * @author Andres Almiray
 * @since 2.4.0
 */
class FXMLFactory extends AbstractNodeFactory {

    private FXMLLoader loader

    FXMLFactory() {
        super(Node)
    }

    FXMLFactory(Class<Node> beanClass) {
        super(beanClass)
    }

    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        Node result
        if (value != null) {
            result = processValue(value)
            if (result == null)
                throw new Exception("In $name value must be an instanceof InputStream or one of its subclasses, java.net.URL, java.net.URI or a String  to be used as embedded content.")
        } else if (attributes.containsKey("location") || attributes.containsKey("url")) {
            def location = attributes.remove("location")
            if (location == null) {
                location = attributes.remove("url")
            }
            if (location instanceof String)
                location = new URL(location)
            result = loadInput(location)
        } else if (attributes.containsKey("uri")) {
            def uri = attributes.remove("uri")
            if (uri instanceof String)
                uri = new URI(uri)
            result = loadInput(uri.toURL())
        } else if (attributes.containsKey("xml")) {
            def xml = attributes.remove("xml")
            result = loadXML(xml)
        } else if (attributes.containsKey("input")) {
            def input = attributes.remove("input")
            result = loadInput(input)
        } else { // default case
            result = new Group()
        }

        return result
    }

    private Node processValue(Object value) {
        Node result = null
        switch (value) {
            case Node:
                result = value
                break
            case CharSequence:
                try {
                    URL url = new URL(value.toString())
                    result = loadInput(url)
                } catch (MalformedURLException mfe) {
                    result = loadXML(value.toString())
                }
                break
            case InputStream:
                result = loadInput(value)
                break
            case URL:
                result = loadInput(value)
                break
            case URI:
                result = loadInput(value.toURL())
                break
        }
        result
    }


    private Object loadXML(String xml) {
        this.@loader = new FXMLLoader()
        def ins = new ByteArrayInputStream(xml.getBytes())
        try {
            return loader.load(ins)
        } finally {
            ins.close()
        }
    }

    private Object loadInput(input) {
        this.@loader = new FXMLLoader()
        return loader.load(input)
    }

    @Override
    void setChild(FactoryBuilderSupport builder, Object parent, Object child) {
        Node childNode = processValue(child)
        if (childNode != null) {
            parent.children.add(childNode)
        } else {
            super.setChild(builder, parent, child)
        }
    }

    @Override
    boolean onNodeChildren(FactoryBuilderSupport builder, Object node, Closure childContent) {
        childContent.delegate = new FXMLDelegate(loader, node, childContent.delegate)
        childContent.call()
        return false
    }

    @Override
    boolean isHandlesNodeChildren() {
        return true
    }
}

class FXMLDelegate {
    FXMLDelegate(FXMLLoader loader, Node node, GroovyObject superObject) {
        this.loader = loader
        this.node = node
        this.superObject = superObject
    }

    private FXMLLoader loader
    private Node node
    private GroovyObject superObject

    @Override
    def getProperty(String property) {
        return this.@loader.namespace[property] ?: this.@node.lookup("#$property") ?: JavaFXUtils.findElement(this.@node, property) ?: this.@superObject.getProperty(property)
    }
}

