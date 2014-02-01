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
package griffon.builder.pivot.factory

import org.apache.pivot.wtk.*

/**
 * @author Andres Almiray
 */
class MenuFactory extends ComponentFactory {
    MenuFactory() {
        super(Menu)
    }

    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        if (value instanceof GString) value = value as String
        if (FactoryBuilderSupport.checkValueIsTypeNotString(value, name, beanClass)) {
            return value
        }
        if (value && !attributes.containsKey('section')) attributes.section = value
        if (!attributes.section) attributes.section = "Menu" + System.currentTimeMillis()
        builder.context.sectionName = attributes.section
        beanClass.newInstance()
    }

    boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map attributes) {
        def section = new Menu.Section()
        section.name = builder.context.section
        node.sections.add(section)
        return super.onHandleNodeAttributes(builder, node, attributes)
    }

    void setParent(FactoryBuilderSupport builder, Object parent, Object node) {
        switch (parent?.getClass()) {
            case MenuButton:
            case MenuPopup:
            case MenuBar.Item:
                parent.setMenu(node)
                break
            case Menu:
                parent.sections.add(node.sections.remove(node.getSection(builder.context.sectionName)))
                break
        }
    }
}

/**
 * @author Andres Almiray
 */
class MenuItemFactory extends ComponentFactory {
    MenuItemFactory() {
        super(Menu.Item, false)
    }

    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        if (value instanceof GString) value = value as String
        if (FactoryBuilderSupport.checkValueIsTypeNotString(value, name, beanClass)) {
            return value
        }
        if (value && !attributes.containsKey('buttonData')) attributes.buttonData = value
        beanClass.newInstance()
    }

    boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map attributes) {
        def section = new Menu.Section()
        section.name = builder.context.section
        node.sections.add(section)
        return super.onHandleNodeAttributes(builder, node, attributes)
    }

    void setParent(FactoryBuilderSupport builder, Object parent, Object node) {
        if (!(parent instanceof Menu)) return
        parent.sections[parent.sections.length - 1].add(node)
    }
}

/**
 * @author Andres Almiray
 */
class MenuBarFactory extends ComponentFactory {
    MenuBarFactory() {
        super(MenuBar, false)
    }

    void setParent(FactoryBuilderSupport builder, Object parent, Object node) {
        if (!(parent instanceof Frame)) return
        parent.setMenuBar(node)
    }
}

/**
 * @author Andres Almiray
 */
class MenuBarItemFactory extends ComponentFactory {
    MenuBarItemFactory() {
        super(MenuBar.Item, false)
    }

    void setParent(FactoryBuilderSupport builder, Object parent, Object node) {
        if (!(parent instanceof MenuBar)) return
        parent.items.add(node)
    }
}
