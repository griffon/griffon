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
package org.codehaus.griffon.runtime.groovy.util;

import griffon.annotations.core.Nonnull;
import griffon.annotations.inject.Evicts;
import griffon.core.resources.ResourceHandler;
import griffon.util.Instantiator;
import griffon.util.ResourceBundleReader;
import griffon.util.groovy.ConfigReader;
import groovy.lang.Script;
import org.codehaus.griffon.runtime.util.ClassResourceBundleLoader;

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
@Evicts("class")
@Named("groovy")
public class GroovyScriptResourceBundleLoader extends ClassResourceBundleLoader {
    protected static final String GROOVY_SUFFIX = ".groovy";
    private final ConfigReader configReader;

    @Inject
    public GroovyScriptResourceBundleLoader(@Nonnull Instantiator instantiator,
                                            @Nonnull ResourceHandler resourceHandler,
                                            @Nonnull ResourceBundleReader resourceBundleReader,
                                            @Nonnull ConfigReader configReader) {
        super(instantiator, resourceHandler, resourceBundleReader);
        this.configReader = requireNonNull(configReader, "Argument 'configReader' must not be null");
    }

    @Nonnull
    @Override
    public Collection<ResourceBundle> load(@Nonnull String name) {
        requireNonBlank(name, ERROR_FILENAME_BLANK);
        List<ResourceBundle> bundles = new ArrayList<>();
        URL resource = getResourceAsURL(name, GROOVY_SUFFIX);
        if (null != resource) {
            bundles.add(new GroovyScriptResourceBundle(configReader, resource));
            return bundles;
        }

        resource = getResourceAsURL(name, CLASS_SUFFIX);
        if (null != resource) {
            String className = name.replace('/', '.');
            try {
                Class<?> klass = loadClass(className);
                if (Script.class.isAssignableFrom(klass)) {
                    bundles.add(new GroovyScriptResourceBundle(configReader, (Class<? extends Script>) klass));
                    return bundles;
                }
            } catch (ClassNotFoundException e) {
                // ignore
            }
        }

        return super.load(name);
    }
}
