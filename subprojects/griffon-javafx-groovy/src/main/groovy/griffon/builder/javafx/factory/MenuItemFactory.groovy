/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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

import griffon.javafx.support.JavaFXAction
import groovyx.javafx.factory.AbstractNodeFactory
import javafx.scene.Node
import javafx.scene.control.CustomMenuItem
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem

import static griffon.builder.javafx.factory.ActionFactory.applyAction
import static griffon.builder.javafx.factory.ActionFactory.extractActionParams

/**
 *
 * @author jimclarke
 */
class MenuItemFactory extends AbstractNodeFactory {
    MenuItemFactory(Class beanClass) {
        super(beanClass)
    }

    MenuItemFactory(Class beanClass, Closure instantiator) {
        super(beanClass, instantiator)
    }

    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        JavaFXAction action = null
        Map actionParams = [:]
        if (value instanceof JavaFXAction) {
            action = value
            value = null
            actionParams = extractActionParams(attributes)
        }

        Object menuItem = instantiate(builder, name, value, attributes)

        if (action) {
            applyAction(menuItem, action, actionParams)
        }

        menuItem
    }

    private Object instantiate(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        if (Menu.isAssignableFrom(beanClass)) {
            return handleMenuNode(builder, name, value, attributes)
        }

        if (value == null) {
            return super.newInstance(builder, name, value, attributes)
        }

        MenuItem mi = null
        switch (value) {
            case CharSequence:
                mi = super.newInstance(builder, name, value, attributes)
                mi.text = value.toString()
                break
            case MenuItem:
                mi = super.newInstance(builder, name, value, attributes)
                mi.items.add(value)
                break
            case Node:
                mi = super.newInstance(builder, name, null, attributes)
                if (mi instanceof CustomMenuItem) {
                    mi.content = node
                } else {
                    mi.graphic = node
                }
                break
            default:
                throw new Exception("In $name value must be an instanceof MenuItem or one of its subclass, a String or a Node to be used as embedded content.")
        }
        mi
    }

    protected Menu handleMenuNode(FactoryBuilderSupport builder, Object name, Object value, Map attributes) {
        if (value == null)
            return beanClass.newInstance("")

        Menu menu = null
        switch (value) {
            case Menu:
                menu = value
                break
            case CharSequence:
                menu = beanClass.newInstance(value.toString())
                break
            case Node:
                menu = beanClass.newInstance("")
                menu.graphic = value
                break
            default:
                throw new Exception("In $name value must be an instanceof Menu or one of its subclasses, a String or a Node to be used as graphic content.")
        }
        menu
    }

    void setChild(FactoryBuilderSupport builder, Object parent, Object child) {
        if (parent instanceof Menu && child instanceof MenuItem) {
            parent.items.add(child)
        } else if (child instanceof Node) {
            if (parent instanceof CustomMenuItem)
                parent.content = child
            else
                parent.graphic = child
        } else if (child instanceof NodeBuilder) {
            parent.graphic = child.build()
        } else {
            super.setChild(builder, parent, child)
        }
    }
}

