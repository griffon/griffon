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
package griffon.builder.swing.factory

import org.codehaus.groovy.runtime.InvokerHelper

import javax.swing.JApplet
import javax.swing.JButton
import javax.swing.JMenuBar
import javax.swing.JToolBar
import javax.swing.SwingUtilities
import java.awt.Component
import java.awt.Window

/**
 * Created by IntelliJ IDEA.
 * @author Danno.Ferrin
 * Date: Sep 4, 2008
 * Time: 8:52:40 PM
 */
@SuppressWarnings("rawtypes")
class ApplicationFactory extends AbstractFactory {
    static final String DELEGATE_PROPERTY_DEFAULT_BUTTON = "_delegateProperty:defaultButton"
    static final String DEFAULT_DELEGATE_PROPERTY_DEFAULT_BUTTON = "defaultButton"

    static final String DELEGATE_PROPERTY_CANCEL_BUTTON = "_delegateProperty:cancelButton"
    static final String DEFAULT_DELEGATE_PROPERTY_CANCEL_BUTTON = "cancelButton"

    static boolean swingXPresent
    static Class<?> jxStatusBarClass
    static {
        try {
            ClassLoader cl = getClass().getClassLoader()
            if (cl) {
                jxStatusBarClass = cl.loadClass('org.jdesktop.swingx.JXStatusBar')
            } else {
                jxStatusBarClass = Class.forName('org.jdesktop.swingx.JXStatusBar', true, ApplicationFactory.classLoader)
            }
            swingXPresent = true
        } catch (Throwable t) {
            swingXPresent = false
        }
    }

    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) {
        def applicationWindow = builder.application.createApplicationContainer([:])
        if (applicationWindow instanceof Window) {
            if (attributes.id) applicationWindow.name = attributes.id
            if (attributes.name) applicationWindow.name = attributes.name
            builder.application.windowManager.attach(applicationWindow.name, applicationWindow)
        }
        def window = getContainingWindow(applicationWindow)

        if (applicationWindow instanceof JApplet && swingXPresent) {
            // bake in some JXFrame stuff if present
            applicationWindow.rootPane = getClass().getClassLoader().loadClass('org.jdesktop.swingx.JXRootPane').newInstance()
        }

        if (!window) return applicationWindow

        if (swingXPresent) {
            builder.context[DELEGATE_PROPERTY_CANCEL_BUTTON] = attributes.remove("cancelButtonProperty") ?: DEFAULT_DELEGATE_PROPERTY_CANCEL_BUTTON

            builder.context.cancelButtonDelegate =
                builder.addAttributeDelegate { myBuilder, node, myAttributes ->
                    if (myAttributes.cancelButton && (node instanceof JButton)) {
                        applicationWindow.rootPaneExt.cancelButton = node
                        myAttributes.remove('cancelButton')
                    }
                }
        }

        builder.context.pack = attributes.remove('pack')
        builder.context.show = attributes.remove('show')
        builder.addDisposalClosure(applicationWindow.&dispose)
        builder.containingWindows.add(window)

        /*
       if(attributes.containsKey('hideBeforeHandler') && builder.app instanceof SwingGriffonApplication) {
           builder.applicationWindow.windowManager.hideBeforeHandler = attributes.remove('hideBeforeHandler')
       }
       */

        builder.context[DELEGATE_PROPERTY_DEFAULT_BUTTON] = attributes.remove("defaultButtonProperty") ?: DEFAULT_DELEGATE_PROPERTY_DEFAULT_BUTTON
        builder.context.defaultButtonDelegate =
            builder.addAttributeDelegate { myBuilder, node, myAttributes ->
                if ((node instanceof JButton) && (builder.containingWindows[-1] == window)) {
                    // in Java 6 use descending iterator
                    ListIterator li = builder.contexts.listIterator()
                    Map context
                    while (li.hasNext()) context = li.next()
                    while (context && context[FactoryBuilderSupport.CURRENT_NODE] != window) {
                        context = li.previous()
                    }
                    def defaultButtonProperty = context[DELEGATE_PROPERTY_DEFAULT_BUTTON] ?: DEFAULT_DELEGATE_PROPERTY_DEFAULT_BUTTON
                    def defaultButton = myAttributes.remove(defaultButtonProperty)
                    if (defaultButton) {
                        applicationWindow.rootPane.defaultButton = node
                    }
                }
            }

        return applicationWindow
    }

    boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map attributes) {
        for (Map.Entry entry : (Set<Map.Entry>) attributes.entrySet()) {
            String property = entry.getKey().toString()
            Object value = entry.getValue()
            // be forgiving on attributes, so an applet can set an icon without punishment, etc
            try {
                InvokerHelper.setProperty(node, property, value)
            } catch (MissingPropertyException mpe) {
                if (mpe.property != property) throw mpe
            }
        }
        return false
    }

    void handleRootPaneExtTasks(FactoryBuilderSupport builder, Window container, Map attributes) {
        container.rootPaneExt.cancelButton = null
        container.rootPaneExt.defaultButton = null
        builder.context.cancelButtonDelegate =
            builder.addAttributeDelegate { myBuilder, node, myAttributes ->
                if (myAttributes.cancelButton && (node instanceof JButton)) {
                    container.rootPaneExt.cancelButton = node
                    myAttributes.remove('cancelButton')
                }
            }

        super.handleRootPaneTasks(builder, container, attributes)
    }

    void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
        def window = getContainingWindow(node)
        builder.removeAttributeDelegate(builder.context.cancelButtonDelegate)
        if (window instanceof Window) {
            def containingWindows = builder.containingWindows
            if (!containingWindows.empty && containingWindows.last == window) {
                containingWindows.removeLast()
            }

            // can't pack or show an applet...

            if (builder.context.pack) {
                window.pack()
            }
            if (builder.context.show) {
                window.visible = true
            }
        }

        builder.removeAttributeDelegate(builder.context.defaultButtonDelegate)
    }

    private getContainingWindow(node) {
        if (node instanceof JApplet || node instanceof Window) {
            return node
        } else if (node instanceof Component) {
            return SwingUtilities.getWindowAncestor(node)
        }
        return null
    }

    void setChild(FactoryBuilderSupport builder, Object parent, Object child) {
        if (!(child instanceof Component) || (child instanceof Window)) {
            return
        }
        if (child instanceof JMenuBar) {
            parent.JMenuBar = child
        } else if (swingXPresent && (child instanceof JToolBar)) {
            parent.rootPane.toolBar = child
        } else if (swingXPresent && (jxStatusBarClass.isAssignableFrom(child.getClass()))) {
            parent.rootPane.statusBar = child
        } else {
            try {
                def constraints = builder.context.constraints
                if (constraints != null) {
                    parent.contentPane.add(child, constraints)
                } else {
                    parent.contentPane.add(child)
                }
            } catch (MissingPropertyException mpe) {
                parent.contentPane.add(child)
            }
        }
    }
}
