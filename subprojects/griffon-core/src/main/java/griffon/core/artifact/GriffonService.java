/*
 * Copyright 2010-2013 the original author or authors.
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

package griffon.core.artifact;

/**
 * Identifies a Service artifact.
 *
 * @author Andres Almiray
 * @since 0.9.1
 */
public interface GriffonService extends GriffonArtifact {
    /**
     * Post initialization callback.<p>
     * Called by the {@code ServiceManager} right after the Service has been instantiated
     *
     * @since 1.2.0
     */
    void serviceInit();

    /**
     * Cleanup callback.<p>
     * Called by the {@code ServiceManager} when the application is shutting down.
     *
     * @since 1.2.0
     */
    void serviceDestroy();
}