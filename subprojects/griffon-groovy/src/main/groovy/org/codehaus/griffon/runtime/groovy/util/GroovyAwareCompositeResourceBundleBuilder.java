/*
 * Copyright 2008-2017 the original author or authors.
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

import griffon.core.resources.ResourceHandler;
import griffon.util.ConfigReader;
import griffon.util.PropertiesReader;
import griffon.util.ResourceBundleReader;
import groovy.lang.Script;
import org.codehaus.griffon.runtime.util.DefaultCompositeResourceBundleBuilder;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class GroovyAwareCompositeResourceBundleBuilder extends DefaultCompositeResourceBundleBuilder {
    protected static final String GROOVY_SUFFIX = ".groovy";
    private final ConfigReader configReader;

    @Inject
    public GroovyAwareCompositeResourceBundleBuilder(@Nonnull ResourceHandler resourceHandler, @Nonnull PropertiesReader propertiesReader, @Nonnull ResourceBundleReader resourceBundleReader, @Nonnull ConfigReader configReader) {
        super(resourceHandler, propertiesReader, resourceBundleReader);
        this.configReader = requireNonNull(configReader, "Argument 'reader' must not be null");
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    protected Collection<ResourceBundle> loadBundleFromClass(@Nonnull String fileName) {
        List<ResourceBundle> bundles = new ArrayList<>();
        URL resource = getResourceAsURL(fileName, GROOVY_SUFFIX);
        if (null != resource) {
            bundles.add(new GroovyScriptResourceBundle(configReader, resource));
            return bundles;
        }

        resource = getResourceAsURL(fileName, CLASS_SUFFIX);
        if (null != resource) {
            String className = fileName.replace('/', '.');
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

        return super.loadBundleFromClass(fileName);
    }
}
