/*
 * Copyright 2008-2013 the original author or authors.
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.WindowConstants;
import java.awt.*;
import java.awt.image.BufferedImage;

import static griffon.util.GriffonNameUtils.isBlank;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * Additional utilities for Swing based applications.
 *
 * @author Andres Almiray
 * @since 0.3.1
 */
public class SwingUtils {

    private static final String ERROR_WINDOW_NULL = "Argument 'window' cannot be null";

    /**
     * Centers a Window on the screen<p>
     * Sets the window on the top left corner if the window's
     * dimensions are bigger than the screen's.
     *
     * @param window a Window object
     */
    public static void centerOnScreen(@Nonnull Window window) {
        requireNonNull(window, ERROR_WINDOW_NULL);

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
     * Centers a JInternalFrame on the screen<p>
     * Sets the internal frame on the top left corner if the frame's
     * dimensions are bigger than the desktop's.
     *
     * @param internalFrame a JInternalFrame object
     */
    public static void centerOnScreen(@Nonnull JInternalFrame internalFrame) {
        requireNonNull(internalFrame, "Argument 'internalFrame' cannot be null");

        JDesktopPane desktop = internalFrame.getDesktopPane();
        if (desktop == null) return;
        Dimension screen = desktop.getSize();
        Point center = new Point(screen.width / 2, screen.height / 2);

        double w = Math.min(internalFrame.getWidth(), screen.width);
        double h = Math.min(internalFrame.getHeight(), screen.height);
        int x = (int) (center.x - (w / 2));
        int y = (int) (center.y - (h / 2));

        Point corner = new Point(
            (x >= 0 ? x : 0),
            (y >= 0 ? y : 0)
        );

        internalFrame.setLocation(corner);
    }

    /**
     * Returns the window's current opacity value.
     *
     * @param window the window on which the opacity will be queried
     * @return the window's opacity value
     */
    public static float getWindowOpacity(@Nonnull Window window) {
        requireNonNull(window, ERROR_WINDOW_NULL);
        return window.getOpacity();
    }

    /**
     * Sets the value for the window's opacity.
     *
     * @param window  the window on which the opacity will be set
     * @param opacity the new opacity value
     */
    public static void setWindowOpacity(@Nonnull Window window, float opacity) {
        requireNonNull(window, ERROR_WINDOW_NULL);
        window.setOpacity(opacity);
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
    @Nullable
    public static Component findComponentByName(@Nonnull String name, @Nonnull Container root) {
        requireNonNull(root, "Argument 'root' cannot be null");
        requireNonBlank(name, "Argument 'name' cannot be blank");
        if (name.equals(root.getName())) {
            return root;
        }

        for (Component comp : root.getComponents()) {
            if (name.equals(comp.getName())) {
                return comp;
            }
            if (comp instanceof Container) {
                Component found = findComponentByName(name, (Container) comp);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    /**
     * Takes a snapshot of the target component.
     *
     * @param component the component to draw
     * @return a Graphics compatible image of the component
     */
    @Nonnull
    public static Image takeSnapshot(@Nonnull Component component) {
        return takeSnapshot(component, false);
    }

    /**
     * Takes a snapshot of the target component.
     *
     * @param component the component to draw
     * @param usePrint  whether <tt>print()</tt> or <tt>paint()</tt> is used to grab the snapshot
     * @return a Graphics compatible image of the component
     */
    @Nonnull
    public static Image takeSnapshot(@Nonnull Component component, boolean usePrint) {
        requireNonNull(component, "Argument 'component' cannot be null");

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

    private static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Creates a Window based on the application's configuration.<p>
     * Class lookup order is<ol>
     * <li>value in app.config.application.frameClass</li>
     * <li>'org.jdesktop.swingx.JXFrame' if SwingX is in the classpath</li>
     * <li>'javax.swing.JFrame'</li>
     *
     * @param application the current running application
     * @return a newly instantiated window according to the application's
     *         preferences
     */
    @Nonnull
    public static Window createApplicationFrame(@Nonnull GriffonApplication application) {
        requireNonNull(application, "Argument 'application' cannot be null");
        JFrame frame = null;
        // try config specified first
        String frameClass = application.getApplicationConfiguration().getAsString("application.frameClass", JFrame.class.getName());
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
}
