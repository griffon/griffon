/*
 * Copyright 2012 the original author or authors.
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
package org.codehaus.griffon.runtime.core;

import griffon.core.ResourceHandler;
import griffon.util.ApplicationClassLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import static org.codehaus.groovy.runtime.DefaultGroovyMethods.toList;

/**
 * Base implementation of the {@link ResourceHandler} interface.
 *
 * @author Andres Almiray
 * @since 0.9.5
 */
public class ResourceLocator implements ResourceHandler {
    public InputStream getResourceAsStream(String name) {
        return classLoader().getResourceAsStream(name);
    }

    public URL getResourceAsURL(String name) {
        return classLoader().getResource(name);
    }

    public List<URL> getResources(String name) {
        Enumeration<URL> resources = null;
        try {
            resources = classLoader().getResources(name);
        } catch (IOException e) {
            // ignore
        }

        return resources != null ? toList(resources) : Collections.<URL>emptyList();
    }

    private ClassLoader classLoader() {
        return ApplicationClassLoader.get();
    }
}
