/*
 * Copyright 2009-2014 the original author or authors.
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

import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * Indicates a type that knows how to load resources from the classpath.
 *
 * @author Andres Almiray
 * @since 0.9.5
 */
public interface ResourceHandler {
    /**
     * Finds the resource with the given name.  A resource is some data
     * (images, audio, text, etc) that can be accessed by class code in a way
     * that is independent of the location of the code.
     * <p/>
     * <p> The name of a resource is a '<tt>/</tt>'-separated path name that
     * identifies the resource.
     *
     * @param name The resource name
     * @return A <tt>URL</tt> object for reading the resource, or
     *         <tt>null</tt> if the resource could not be found.
     */
    URL getResourceAsURL(String name);

    /**
     * Returns an input stream for reading the specified resource.
     *
     * @param name The resource name
     * @return An input stream for reading the resource, or <tt>null</tt>
     *         if the resource could not be found
     */
    InputStream getResourceAsStream(String name);

    /**
     * Finds all the resources with the given name. A resource is some data
     * (images, audio, text, etc) that can be accessed by class code in a way
     * that is independent of the location of the code.
     * <p/>
     * <p>The name of a resource is a <tt>/</tt>-separated path name that
     * identifies the resource.
     *
     * @param name The resource name
     * @return An java.util.List of {@link java.net.URL <tt>URL</tt>} objects for
     *         the resource.  If no resources could  be found, the list
     *         will be empty.  Resources that the class loader doesn't have
     *         access to will not be in the list.
     */
    List<URL> getResources(String name);
}
