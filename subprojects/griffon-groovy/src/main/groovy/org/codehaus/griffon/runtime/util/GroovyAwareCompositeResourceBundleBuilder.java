/*
 * Copyright 2010-2014 the original author or authors.
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

package org.codehaus.griffon.runtime.util;

import griffon.core.resources.ResourceHandler;
import griffon.util.ConfigReader;
import groovy.lang.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOG = LoggerFactory.getLogger(GroovyAwareCompositeResourceBundleBuilder.class);

    protected static final String GROOVY_SUFFIX = ".groovy";
    private final ConfigReader configReader;

    @Inject
    public GroovyAwareCompositeResourceBundleBuilder(@Nonnull ResourceHandler resourceHandler, @Nonnull ConfigReader configReader) {
        super(resourceHandler);
        this.configReader = requireNonNull(configReader, "Argument 'reader' cannot be null");
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
                    bundles.add(new GroovyScriptResourceBundle(configReader, (Class<? extends Script>)klass));
                    return bundles;
                }
            } catch (ClassNotFoundException e) {
                // ignore
            }
        }

        return super.loadBundleFromClass(fileName);
    }
}
