/*
 * Copyright 2008-2016 the original author or authors.
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
package griffon.core;

import javax.annotation.Nonnull;

/**
 * A ShutdownHandler may prevent the application from exiting.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public interface ShutdownHandler {
    /**
     * Asks this handler if the application's shutdown sequence can proceed or not.<p>
     * Return <tt>false</tt> if the shutdown sequence must be aborted.
     *
     * @param application the current running application
     * @return true if the shutdown sequence can proceed, false otherwise
     */
    boolean canShutdown(@Nonnull GriffonApplication application);

    /**
     * Called when the shutdown sequence continues
     *
     * @param application the current running application
     */
    void onShutdown(@Nonnull GriffonApplication application);
}
