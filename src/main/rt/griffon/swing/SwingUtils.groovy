/*
 * Copyright 2008-2011 the original author or authors.
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
package griffon.swing

import java.awt.Window
import java.awt.Dimension
import java.awt.Point
import java.awt.Toolkit
import java.awt.Component
import java.awt.Container
import java.awt.GraphicsEnvironment
import javax.swing.JFrame
import javax.swing.WindowConstants
import griffon.core.GriffonApplication

import static griffon.util.GriffonApplicationUtils.isJdk16
import static griffon.util.GriffonApplicationUtils.isJdk17

/**
 * Additional utilities for Swing based applications.
 *
 * @author Andres Almiray
 * @since 0.3.1
 */
class SwingUtils {
    /**
     * Centers a Window on the screen<p>
     * Sets the window on the top left corner if the window's 
     * dimensions are bigger than the screen's.
     *
     * @param window a Window object
     */
    static void centerOnScreen(Window window) {
        Point center = GraphicsEnvironment.localGraphicsEnvironment.centerPoint
        Dimension screen = Toolkit.defaultToolkit.screenSize

        double w = Math.min(window.width, screen.width)
        double h = Math.min(window.height, screen.height)
        double x = center.x - (w/2)
        double y = center.y - (h/2)
         
        Point corner = new Point(
            (x >= 0 ? x : 0) as int,
            (y >= 0 ? y : 0) as int
        )

        window.setLocation(corner)
    }

    /**
     * Creates a Window based on the application's configurarion.<p>
     * Class lookup order is<ol>
     * <li>value in app.config.application.frameClass</li>
     * <li>'org.jdesktop.swingx.JXFrame' if SwingX is in teh classpath</li>
     * <li>'javax.swing.JFrame'</li>
     *
     * @param app the current running application
     * @return a newly instantiated window according to the application's
     *         preferences
     */
    static Window createApplicationFrame(GriffonApplication app) {
        Object frame = null
        // try config specified first
        if (app.config.application?.frameClass) {
            try {
                ClassLoader cl = getClass().getClassLoader()
                if (cl) {
                    frame = cl.loadClass(app.config.application.frameClass).newInstance()
                } else {
                    frame = Class.forName(app.config.application.frameClass).newInstance()
                }
            } catch (Throwable ignored) {
                // ignore
            }
        }
        if (frame == null) {
            // JXFrame, it's nice.  Try it!
            try {
                ClassLoader cl = getClass().getClassLoader()
                if (cl) {
                    frame = cl.loadClass('org.jdesktop.swingx.JXFrame').newInstance()
                } else {
                    frame = Class.forName('org.jdesktop.swingx.JXFrame').newInstance()
                }
            } catch (Throwable ignored) {
                // ignore
            }
            // this will work for sure
            if (frame == null) {
                frame = new JFrame()
            }

            // do some standard tweaking
            // frame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
            frame.defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
        }
        return frame
    }

    /**
     * Finds out if translucency is supported by the current platform.
     *
     * @return true if Translucency.TRANSLUCENT is supported, false otherwise
     */
    static boolean isTranslucencySupported() {
        if(isJdk17) {
            return true
        } else if(isJdk16) {
            Class awtUtilities = Class.forName('com.sun.awt.AWTUtilities')
            Class translucency = Class.forName('com.sun.awt.AWTUtilities$Translucency')
            return awtUtilities.isTranslucencySupported(translucency.TRANSLUCENT)
        }
        return false
    }

    /**
     * Returns the window's current opacity value.
     *
     * @param window the window on which the opacity will be queried
     * @return the window's opacity value
     */
    static float getWindowOpacity(Window window) {
        if(isJdk17) {
            return window.getOpacity()
        } else if(isJdk16) {
            Class awtUtilities = Class.forName('com.sun.awt.AWTUtilities')
            return awtUtilities.getWindowOpacity(window)
        }
        return 1.0f
    }

    /**
     * Sets the value for the window's opacity.
     *
     * @param window the window on which the opacity will be set
     * @param opacity the new opacity value
     */
    static void setWindowOpacity(Window window, float opacity) {
        if(isJdk17) {
            window.setOpacity(opacity)
        } else if(isJdk16) {
            Class awtUtilities = Class.forName('com.sun.awt.AWTUtilities')
            awtUtilities.setWindowOpacity(window, opacity)
        }
    }

    /**
     * Searches a component by name in a particular component hierarrchy.<p>
     * A component must have a value for its <tt>name</tt> property if it's
     * to be found with this method.<br/>
     * This method performs a depth-first search.
     *
     * @param name the value of the component's <tt>name</tt> property
     * @param root the root of the component hierarchy from where searching
     *             searching should start
     * @return the component reference if found, null otherwise
     */
    static Component findComponentByName(String name, Container root) {
    if(root?.name == name) return root
        Component component = null
        for(comp in root.components) {
            Component found = findComponentByName(name, comp)
            if(found) {
                component = found
                break
            }
        }
        return component
    }
}