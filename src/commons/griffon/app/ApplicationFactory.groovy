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

import java.awt.Component
import java.awt.Window
import javax.swing.*
import org.codehaus.groovy.runtime.InvokerHelper

/**
 * Created by IntelliJ IDEA.
 *@author Danno.Ferrin
 * Date: Sep 4, 2008
 * Time: 8:52:40 PM
 */
class ApplicationFactory extends AbstractFactory {

    public static final String DELEGATE_PROPERTY_DEFAULT_BUTTON = "_delegateProperty:defaultButton";
    public static final String DEFAULT_DELEGATE_PROPERTY_DEFAULT_BUTTON = "defaultButton";

    public static final String DELEGATE_PROPERTY_CANCEL_BUTTON = "_delegateProperty:cancelButton";
    public static final String DEFAULT_DELEGATE_PROPERTY_CANCEL_BUTTON = "cancelButton";

    static boolean swingXPresent
    static Class jxStatusBarClass
    static {
        try {
            ClassLoader cl = getClass().getClassLoader();
            if (cl) {
                jxStatusBarClass = cl.loadClass('org.jdesktop.swingx.JXStatusBar')
            } else {
                jxStatusBarClass = Class.forName('org.jdesktop.swingx.JXStatusBar') 
            }
            swingXPresent = true
        } catch (Throwable t) {
            swingXPresent = false
        }
    }

    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) {
        def application = builder.app.createApplicationContainer()

        if ((application instanceof JApplet) && swingXPresent) {
            // bake in some JXFrame stuff if present
            application.rootPane = getClass().getClassLoader().loadClass('org.jdesktop.swingx.JXRootPane').newInstance()
        }

        if (swingXPresent) {
            builder.context[DELEGATE_PROPERTY_CANCEL_BUTTON] = attributes.remove("cancelButtonProperty") ?: DEFAULT_DELEGATE_PROPERTY_CANCEL_BUTTON


            builder.context.cancelButtonDelegate =
                builder.addAttributeDelegate {myBuilder, node, myAttributes ->
                    if (myAttributes.cancelButton && (node instanceof JButton)) {
                        application.rootPaneExt.cancelButton = node
                        myAttributes.remove('cancelButton')
                    }
                }
        }
        
        def window = application
        if (!application instanceof Window) {
            window = SwingUtilities.getWindowAncestor(application)
        } else {
            builder.context.pack = attributes.remove('pack')
            builder.context.show = attributes.remove('show')
            builder.addDisposalClosure(application.&dispose)
        }
        builder.containingWindows.add(window)

        builder.context[DELEGATE_PROPERTY_DEFAULT_BUTTON] = attributes.remove("defaultButtonProperty") ?: DEFAULT_DELEGATE_PROPERTY_DEFAULT_BUTTON
        builder.context.defaultButtonDelegate =
            builder.addAttributeDelegate {myBuilder, node, myAttributes ->
                if ((node instanceof JButton) && (builder.containingWindows[-1] == window)) {
                    // in Java 6 use descending iterator
                    ListIterator li = builder.contexts.listIterator();
                    Map context
                    while (li.hasNext()) context = li.next()
                    while (context && context[FactoryBuilderSupport.CURRENT_NODE] != window) {
                        context = li.previous()
                    }
                    def defaultButtonProperty = context[DELEGATE_PROPERTY_DEFAULT_BUTTON] ?: DEFAULT_DELEGATE_PROPERTY_DEFAULT_BUTTON
                    def defaultButton = myAttributes.remove(defaultButtonProperty)
                    if (defaultButton) {
                        application.rootPane.defaultButton = node
                    }
                }
            }

        return application
    }

    public boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map attributes) {
        for (Map.Entry entry : (Set<Map.Entry>) attributes.entrySet()) {
            String property = entry.getKey().toString();
            Object value = entry.getValue();
            // be forgiving on attributes, so an applet can set an icon without punishment, etc
            try {
                InvokerHelper.setProperty(node, property, value);
            } catch (MissingPropertyException mpe) {
                if (mpe.property != property) throw mpe
            }
        }
        return false
    }

    public void handleRootPaneExtTasks(FactoryBuilderSupport builder, Window container, Map attributes) {
        container.rootPaneExt.cancelButton = null
        container.rootPaneExt.defaultButton = null
        builder.context.cancelButtonDelegate =
            builder.addAttributeDelegate {myBuilder, node, myAttributes ->
                if (myAttributes.cancelButton && (node instanceof JButton)) {
                    container.rootPaneExt.cancelButton = node
                    myAttributes.remove('cancelButton')
                }
            }

        super.handleRootPaneTasks(builder, container, attributes)
    }


    public void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
        builder.removeAttributeDelegate(builder.context.cancelButtonDelegate)
        if (node instanceof Window) {
            def containingWindows = builder.containingWindows
            if (!containingWindows.empty && containingWindows.last == node) {
                containingWindows.removeLast();
            }
        }

        // can't pack or show an applet...
        if (node instanceof Window) {
            if (builder.context.pack) {
                node.pack()
            }
            if (builder.context.show) {
                node.visible = true
            }  
        }

        builder.removeAttributeDelegate(builder.context.defaultButtonDelegate)
    }


    public void setChild(FactoryBuilderSupport builder, Object parent, Object child) {
        if (!(child instanceof Component) || (child instanceof Window)) {
            return;
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