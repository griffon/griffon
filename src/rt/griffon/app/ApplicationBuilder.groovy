/*
 * Copyright 2008 the original author or authors.
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
package griffon.app

import groovy.swing.factory.CollectionFactory
import javax.swing.JComponent
import javax.swing.KeyStroke

/**
 * Created by IntelliJ IDEA.
 *@author Danno.Ferrin
 * Date: Sep 4, 2008
 * Time: 10:22:22 AM
 */
class ApplicationBuilder extends FactoryBuilderSupport {
    private static final Random random = new Random()

    public ApplicationBuilder(boolean init = true) {
        super(init)
    }

    public void registerVisuals() {
        registerFactory 'application', new ApplicationFactory()
        addAttributeDelegate(ApplicationBuilder.&clientPropertyAttributeDelegate)
        registerFactory("noparent", new CollectionFactory())
        registerExplicitMethod("keyStrokeAction", this.&createKeyStrokeAction)
    }

    public static clientPropertyAttributeDelegate(def builder, def node, def attributes) {
        def clientPropertyMap = attributes.remove("clientProperty")
        clientPropertyMap.each { key, value ->
           node.putClientProperty key, value
        }
        attributes.findAll { it.key =~ /clientProperty(\w)/ }.each { key, value ->
           attributes.remove(key)
           node.putClientProperty(key - "clientProperty", value)
        }
    }

    public static createKeyStrokeAction( Map attributes, JComponent component = null ) {
        component = findTargetComponent(attributes, component)
        if( !attributes.containsKey("keyStroke") ) {
            throw new RuntimeException("You must define a value for keyStroke:")
        }
        if( !attributes.containsKey("action") ) {
            throw new RuntimeException("You must define a value for action:")
        }

        def condition = attributes.remove("condition") ?: JComponent.WHEN_FOCUSED
        if( condition instanceof String ) {
            condition = condition.toUpperCase().replaceAll(" ","_")
            if( !condition.startsWith("WHEN_") ) condition = "WHEN_"+condition
        }
        switch(condition) {
            case JComponent.WHEN_FOCUSED:
            case JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT:
            case JComponent.WHEN_IN_FOCUSED_WINDOW:
                // everything is fine, no further processing
                break
            case "WHEN_FOCUSED":
                condition = JComponent.WHEN_FOCUSED
                break
            case "WHEN_ANCESTOR_OF_FOCUSED_COMPONENT":
                condition = JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
                break
            case "WHEN_IN_FOCUSED_WINDOW":
                condition = JComponent.WHEN_IN_FOCUSED_WINDOW
                break
            default:
                // let's be lenient and asign WHEN_FOCUSED by default
                condition = JComponent.WHEN_FOCUSED
        }
        def actionKey = attributes.remove("actionKey")
        if( !actionKey ) actionKey = "Action"+Math.abs(random.nextLong())

        def keyStroke = attributes.remove("keyStroke")
        // accept String, KeyStroke, List<String>, List<KeyStroke>
        def action = attributes.remove("action")

        if( keyStroke instanceof String ) keyStroke = [keyStroke]
        keyStroke.each { ks ->
            switch(ks) {
                case KeyStroke:
                    component.getInputMap(condition).put(ks, actionKey)
                    component.actionMap.put(actionKey, action)
                    break
                case String:
                    component.getInputMap(condition).put(shortcut(ks), actionKey)
                    component.actionMap.put(actionKey, action)
                    break
                default:
                    throw new RuntimeException("Can not apply ${ks} as a KeyStroke value.")
            }
        }
    }

    private static findTargetComponent( Map attributes, JComponent component ) {
        if( component ) return component
        if( attributes.containsKey("component") ) {
            def c = attributes.remove("component")
            if( !(c instanceof JComponent) ) {
                throw new RuntimeException("The property component: is not of type JComponent.")
            }
            return c
        }
        def c = getCurrent()
        if( c instanceof JComponent ) {
            return c
        }
        throw new RuntimeException("You must define one of the following: a value of type JComponent, a component: attribute or nest this node inside another one that produces a JComponent.")
    }
}