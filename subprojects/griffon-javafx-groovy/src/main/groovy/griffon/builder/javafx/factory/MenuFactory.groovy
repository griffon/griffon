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
package griffon.builder.javafx.factory

import griffon.javafx.support.JavaFXAction
import groovyx.javafx.factory.AbstractNodeFactory
import javafx.scene.control.ButtonBase
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import javafx.scene.control.SplitMenuButton

import static griffon.builder.javafx.factory.ActionFactory.applyAction
import static griffon.builder.javafx.factory.ActionFactory.extractActionParams

/**
 *
 * @author jimclarke
 */
class MenuFactory extends AbstractNodeFactory {
    MenuFactory(Class beanClass) {
        super(beanClass)
    }

    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        JavaFXAction action = null
        Map actionParams = [:]
        if (value instanceof JavaFXAction) {
            action = value
            value = null
            actionParams = extractActionParams(attributes)
        }

        Object menu = super.newInstance(builder, name, value, attributes)

        if (menu instanceof ButtonBase && action) {
            applyAction(menu, action, actionParams)
        }

        if (value instanceof CharSequence) {
            switch (menu) {
                case MenuButton:
                case SplitMenuButton:
                    menu.text = value.toString()
                    break
            }

        }
        menu
    }

    void setChild(FactoryBuilderSupport builder, Object parent, Object child) {
        if (parent instanceof MenuBar && child instanceof Menu) {
            parent.menus.add(child)
        } else if (child instanceof MenuItem) {
            parent.items.add(child)
        } else {
            super.setChild(builder, parent, child)
        }
    }
}

