/*
 * Copyright 2010 the original author or authors.
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
package griffon.core;

/**
 * A ShutdownHanlder may prevent the application from exiting.
 *
 * @author Andres Almiray
 * @since 0.3.1
 */
public interface ShutdownHandler {
    /**
     * Asks this handler if the application's shutdown sequence can proceed or not.<p>
     * Return <tt>false</tt> if the shutdown sequence must be aborted.
     *
     * @param application the current running application
     * @return true if the shutdown sequence can proceed, false otherwise
     */
    boolean canShutdown(GriffonApplication application);    

    /**
     * Called when the shutdown sequence continues
     *
     * @param application the current running application
     */
    void onShutdown(GriffonApplication application);    
}
