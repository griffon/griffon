/*
 * Copyright 2010-2012 the original author or authors.
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

import griffon.core.i18n.MessageSource;
import groovy.lang.MetaClass;
import org.slf4j.Logger;

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
 * @since 0.9.1
 */
public interface GriffonArtifact extends ApplicationHandler, ThreadingHandler, MVCHandler, ResourceHandler {
    /**
     * Creates a new instance of the specified class and type.
     *
     * @param clazz the Class to be instantiated
     * @param type  a logical type, such as 'controller'. May be null.
     * @return the newly created instance
     */
    Object newInstance(Class clazz, String type);

    /**
     * Gets the {@code MetaClass} of this artifact.<p>
     * It should delegate to its GriffonClass to get the real MetaClass.
     *
     * @return The MetaClass for this Griffon class
     */
    MetaClass getMetaClass();

    /**
     * Returns the <tt>GriffonClass</tt> associated with this artifact.
     *
     * @return the <tt>GriffonClass</tt> associated with this artifact
     */
    GriffonClass getGriffonClass();

    /**
     * Returns a Logger instance suitable for this Artifact.<p>
     * The Logger is configured with the following prefix 'griffon.app.&lt;type&gt;'
     * where &lt;type&gt; stands for the artifact's type.<p>
     * Example: the Logger for class com.acme.SampleController will be configured for
     * 'griffon.app.controller.com.acme.SampleController'.
     *
     * @return a Logger instance associated with this artifact.
     * @since 0.9.2
     */
    Logger getLog();
}
