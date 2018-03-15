/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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
package org.codehaus.griffon.runtime.util;

import griffon.core.resources.ResourceHandler;
import griffon.util.Instantiator;
import griffon.util.ResourceBundleReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.11.0
 */
@Named("class")
public class ClassResourceBundleLoader extends AbstractResourceBundleLoader {
    private static final Logger LOG = LoggerFactory.getLogger(ClassResourceBundleLoader.class);
    protected static final String CLASS_SUFFIX = ".class";

    protected final Instantiator instantiator;
    protected final ResourceBundleReader resourceBundleReader;

    @Inject
    public ClassResourceBundleLoader(@Nonnull Instantiator instantiator,
                                     @Nonnull ResourceHandler resourceHandler,
                                     @Nonnull ResourceBundleReader resourceBundleReader) {
        super(resourceHandler);
        this.instantiator = requireNonNull(instantiator, "Argument 'instantiator' must not be null");
        this.resourceBundleReader = requireNonNull(resourceBundleReader, "Argument 'resourceBundleReader' must not be null");
    }

    @Nonnull
    @Override
    public Collection<ResourceBundle> load(@Nonnull String name) {
        requireNonBlank(name, ERROR_FILENAME_BLANK);
        List<ResourceBundle> bundles = new ArrayList<>();
        URL resource = getResourceAsURL(name, CLASS_SUFFIX);
        if (null != resource) {
            String url = resource.toString();
            String className = name.replace('/', '.');
            try {
                Class klass = loadClass(className);
                if (ResourceBundle.class.isAssignableFrom(klass)) {
                    bundles.add(resourceBundleReader.read(newInstance(klass)));
                }
            } catch (ClassNotFoundException e) {
                // ignore
            } catch (Exception e) {
                LOG.warn("An error occurred while loading resource bundle " + name + " from " + url, e);
            }
        }
        return bundles;
    }

    @Nonnull
    protected Class<?> loadClass(String className) throws ClassNotFoundException {
        return getResourceHandler().classloader().loadClass(className);
    }

    @Nonnull
    protected ResourceBundle newInstance(Class<?> klass) {
        return instantiator.instantiate((Class<? extends ResourceBundle>) klass);
    }
}
