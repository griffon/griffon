/*
 * Copyright 2010-2011 the original author or authors.
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
 * See the License for the specific language govnerning permissions and
 * limitations under the License.
 */
package griffon.swing;

import griffon.core.GriffonApplication;

/**
 * Defines an additional contract that all Swing based application should follow.<p>
 * Windows in a Swing application should be handled by a {@code WindowManager}, which in
 * turn requires a concrete implementation of the {@code WindowDisplayHandler} to be available
 * on the target application.
 *
 * @author Andres Almiray
 * @see griffon.swing.WindowManager
 * @see griffon.swing.WindowDisplayHandler
 * @since 0.9.1
 */
public interface SwingGriffonApplication extends GriffonApplication {
    /**
     * Returns the {@code WindowManager} associated with this application.<p>
     * Every {@code GriffonSwingApplication} must have a non-null instance.
     *
     * @return the current WindowManager instance.
     */
    WindowManager getWindowManager();

    /**
     * Returns the {@code WindowDisplayHandler} defined for this application.<p>
     * An application implementation may opt for ignoring this property, in which case
     * a {@code DefaultWindowDisplayHandler} should be used.
     *
     * @return the current WindowDisplayHandler instance, may return null.
     */
    WindowDisplayHandler getWindowDisplayHandler();

    /**
     * Sets the {@code WindowDisplayHandler} to be used in conjuction with {@code WindowManager}
     * to handle window showing/hiding.<p>
     * If set to null the application should revert to using an instance of
     * {@code DefaultWindowDisplayHandler}.
     *
     * @param windowDisplayHandler the instance to use, may be null.
     */
    void setWindowDisplayHandler(WindowDisplayHandler windowDisplayHandler);

    /**
     * Resolves the {@code WindowDisplayHandler} to be used with {@code WindowManager}.<p>
     * Should <b>NEVER</b> return null. If no custom {@code WindowDisplayHandler} has been defined
     * for this application then this method should return an instance of {@code DefaulWindowDisplayHandler}.
     *
     * @return a non-null WindowDisplayHandler instance.
     */
    WindowDisplayHandler resolveWindowDisplayHandler();
}