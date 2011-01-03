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
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package griffon.util;

import griffon.core.GriffonApplication;

/**
 * Utility class that holds a reference to the current application.<p>
 * Usage of this class with Griffon artifacts/addons instances is highly discouraged
 * as those instances should have an <tt>app</tt> property of their own. This class
 * is provided as a convenience for non Griffon artifacts.
 *
 * @author Andres Almiray
 * @since 0.9.1
 */
public class ApplicationHolder {
    private static GriffonApplication applicationInstance;

    /**
     * Returns the current running application.
     * @return a reference to the current running application
     */
    public static synchronized GriffonApplication getApplication() {
        return applicationInstance;
    }

    /**
     * Stores a reference to an application.
     *
     * @param application an application instance
     */
    public static synchronized void setApplication(GriffonApplication application) {
        applicationInstance = application;
    }
}
