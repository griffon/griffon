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
package griffon.core.artifact;

import griffon.core.GriffonApplication;
import griffon.core.mvc.MVCHandler;
import griffon.core.resources.ResourceHandler;
import griffon.core.threading.ThreadingHandler;
import org.slf4j.Logger;

import javax.annotation.Nonnull;

/**
 * Identifies an object as a Griffon artifact.<p>
 * Griffon artifacts are usually placed under the special "griffon-app" directory
 * that every application has. They are also grouped together in in a subdirectory that
 * clearly identifies their nature. For example "griffon-app/controllers" contains all
 * Controller artifacts.<p>
 * Implementing this interface for a custom artifact definition is highly recommended
 * but not enforced.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public interface GriffonArtifact extends ThreadingHandler, MVCHandler, ResourceHandler {
    /**
     * Reference to the current {@code GriffonApplication}
     *
     * @return the currently running application
     */
    GriffonApplication getApplication();

    /**
     * Returns the <tt>GriffonClass</tt> associated with this artifact.
     *
     * @return the <tt>GriffonClass</tt> associated with this artifact
     */
    @Nonnull
    GriffonClass getGriffonClass();

    /**
     * Returns a Logger instance suitable for this Artifact.<p>
     * The Logger is configured with the following prefix 'griffon.app.&lt;type&gt;'
     * where &lt;type&gt; stands for the artifact's type.<p>
     * Example: the Logger for class com.acme.SampleController will be configured for
     * 'griffon.app.controller.com.acme.SampleController'.
     *
     * @return a Logger instance associated with this artifact.
     */
    @Nonnull
    Logger getLog();
}
