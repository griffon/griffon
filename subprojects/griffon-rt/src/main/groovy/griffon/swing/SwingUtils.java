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
package griffon.swing;

import griffon.core.GriffonApplication;
import griffon.util.ConfigUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static griffon.util.GriffonApplicationUtils.isJdk16;
import static griffon.util.GriffonApplicationUtils.isJdk17;
import static griffon.util.GriffonClassUtils.invokeInstanceMethod;
import static griffon.util.GriffonClassUtils.invokeStaticMethod;
import static griffon.util.GriffonNameUtils.isBlank;

/**
 * Additional utilities for Swing based applications.
 *
 * @author Andres Almiray
 * @since 0.3.1
 */
public class SwingUtils {
    /**
     * Centers a Window on the screen<p>
     * Sets the window on the top left corner if the window's
     * dimensions are bigger than the screen's.
     *
     * @param window a Window object
     */
    public static void centerOnScreen(Window window) {
        Point center = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        double w = Math.min(window.getWidth(), screen.width);
        double h = Math.min(window.getHeight(), screen.height);
        int x = (int) (center.x - (w / 2));
        int y = (int) (center.y - (h / 2));

        Point corner = new Point(
                (x >= 0 ? x : 0),
                (y >= 0 ? y : 0)
        );

        window.setLocation(corner);
    }

    /**
     * Creates a Window based on the application's configuration.<p>
     * Class lookup order is<ol>
     * <li>value in app.config.application.frameClass</li>
     * <li>'org.jdesktop.swingx.JXFrame' if SwingX is in the classpath</li>
     * <li>'javax.swing.JFrame'</li>
     *
     * @param app the current running application
     * @return a newly instantiated window according to the application's
     *         preferences
     */
    public static Window createApplicationFrame(GriffonApplication app) {
        JFrame frame = null;
        // try config specified first
        String frameClass = (String) ConfigUtils.getConfigValue(app.getConfig(), "application.frameClass");
        if (!isBlank(frameClass)) {
            try {
                ClassLoader cl = SwingUtils.class.getClassLoader();
                if (cl != null) {
                    frame = (JFrame) cl.loadClass(frameClass).newInstance();
                } else {
                    frame = (JFrame) Class.forName(frameClass).newInstance();
                }
            } catch (Throwable ignored) {
                // ignore
            }
        }
        if (frame == null) {
            // JXFrame, it's nice.  Try it!
            try {
                ClassLoader cl = SwingUtils.class.getClassLoader();
                if (cl != null) {
                    frame = (JFrame) cl.loadClass("org.jdesktop.swingx.JXFrame").newInstance();
                } else {
                    frame = (JFrame) Class.forName("org.jdesktop.swingx.JXFrame").newInstance();
                }
            } catch (Throwable ignored) {
                // ignore
            }
            // this will work for sure
            if (frame == null) {
                frame = new JFrame();
            }

            // do some standard tweaking
            frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        }
        return frame;
    }

    /**
     * Finds out if translucency is supported by the current platform.
     *
     * @return true if Translucency.TRANSLUCENT is supported, false otherwise
     */
    public static boolean isTranslucencySupported() {
        if (isJdk17()) {
            return true;
        } else if (isJdk16()) {
            Class awtUtilities = loadClass("com.sun.awt.AWTUtilities");
            Class translucency = loadClass("com.sun.awt.AWTUtilities$Translucency");
            return (Boolean) invokeStaticMethod(awtUtilities, "isTranslucencySupported", Enum.valueOf(translucency, "TRANSLUCENT"));
        }
        return false;
    }

    /**
     * Returns the window's current opacity value.
     *
     * @param window the window on which the opacity will be queried
     * @return the window's opacity value
     */
    public static float getWindowOpacity(Window window) {
        Float value = 1.0f;
        if (isJdk17()) {
            value = (Float) invokeInstanceMethod(window, "getOpacity");
        } else if (isJdk16()) {
            Class awtUtilities = loadClass("com.sun.awt.AWTUtilities");
            value = (Float) invokeStaticMethod(awtUtilities, "getWindowOpacity", window);
        }
        return value;
    }

    /**
     * Sets the value for the window's opacity.
     *
     * @param window  the window on which the opacity will be set
     * @param opacity the new opacity value
     */
    public static void setWindowOpacity(Window window, float opacity) {
        if (isJdk17()) {
            invokeInstanceMethod(window, "setOpacity", opacity);
        } else if (isJdk16()) {
            Class awtUtilities = loadClass("com.sun.awt.AWTUtilities");
            invokeStaticMethod(awtUtilities, "setWindowOpacity", window, opacity);
        }
    }

    /**
     * Searches a component by name in a particular component hierarchy.<p>
     * A component must have a value for its <tt>name</tt> property if it's
     * to be found with this method.<br/>
     * This method performs a depth-first search.
     *
     * @param name the value of the component's <tt>name</tt> property
     * @param root the root of the component hierarchy from where searching
     *             searching should start
     * @return the component reference if found, null otherwise
     */
    public static Component findComponentByName(String name, Container root) {
        if (root == null || isBlank(root.getName())) return root;
        Component component = null;
        for (Component comp : root.getComponents()) {
            if (comp instanceof Container) {
                Component found = findComponentByName(name, (Container) comp);
                if (found != null) {
                    component = found;
                    break;
                }
            }
        }
        return component;
    }

    /**
     * Takes a snapshot of the target component.
     *
     * @param component the component to draw
     * @return a Graphics compatible image of the component
     */
    public static Image takeSnapshot(Component component) {
        return takeSnapshot(component, false);
    }

    /**
     * Takes a snapshot of the target component.
     *
     * @param component the component to draw
     * @param usePrint  whether <tt>print()</tt> or <tt>paint()</tt> is used to grab the snapshot
     * @return a Graphics compatible image of the component
     */
    public static Image takeSnapshot(Component component, boolean usePrint) {
        BufferedImage image = null;
        GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = genv.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();

        if (gc.getColorModel().hasAlpha()) {
            image = gc.createCompatibleImage(
                    (int) component.getSize().getWidth(),
                    (int) component.getSize().getHeight());
        } else {
            image = new BufferedImage(
                    (int) component.getSize().getWidth(),
                    (int) component.getSize().getHeight(),
                    BufferedImage.TYPE_INT_ARGB);
        }

        Graphics g = image.getGraphics();
        if (usePrint) {
            component.print(g);
        } else {
            component.paint(g);
        }
        g.dispose();

        return image;
    }

    private static Class loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
