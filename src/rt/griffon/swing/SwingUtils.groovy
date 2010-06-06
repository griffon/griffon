/*
 * Copyright 2008-2010 the original author or authors.
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
import java.awt.GraphicsEnvironment
import javax.swing.JFrame
import griffon.core.GriffonApplication

/**
 * Additional utilities for Swing based applications.
 *
 * @author Andres Almiray
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
	        (x >= 0 ? x : 0) as double,
	        (y >= 0 ? y : 0) as double
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
                ClassLoader cl = getClass().getClassLoader();
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
                ClassLoader cl = getClass().getClassLoader();
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
            frame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        }
        return frame
    }
}