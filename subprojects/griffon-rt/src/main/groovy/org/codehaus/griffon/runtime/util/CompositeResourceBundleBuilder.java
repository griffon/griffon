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

package org.codehaus.griffon.runtime.util;

import griffon.util.ApplicationClassLoader;
import org.codehaus.griffon.runtime.core.ResourceLocator;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static griffon.util.GriffonNameUtils.isBlank;

/**
 * @author Andres Almiray
 * @since 1.5.0
 */
public class CompositeResourceBundleBuilder {
    private final ResourceLocator RESOURCE_LOCATOR = new ResourceLocator();

    private static final String PROPERTIES_SUFFIX = ".properties";
    private static final String GROOVY_SUFFIX = ".groovy";
    private static final String CLASS_SUFFIX = ".class";

    public ResourceBundle create(String basename) {
        return create(basename, Locale.getDefault());
    }

    public ResourceBundle create(String basename, Locale locale) {
        if (isBlank(basename)) {
            throw new IllegalArgumentException("Cannot create CompositeResourceBundle with basename = '" + basename + "'");
        }

        String[] combinations = {
            locale.getLanguage() + "_" + locale.getCountry() + "_" + locale.getVariant(),
            locale.getLanguage() + "_" + locale.getCountry(),
            locale.getLanguage()
        };

        basename = basename.replace('.', '/');
        List<ResourceBundle> bundles = new ArrayList<ResourceBundle>();
        for (String suffix : combinations) {
            if (suffix.endsWith("_")) continue;
            bundles.addAll(loadBundleFromScript(basename + "_" + suffix));
            bundles.addAll(loadBundleFromProperties(basename + "_" + suffix));
        }
        bundles.addAll(loadBundleFromScript(basename));
        bundles.addAll(loadBundleFromProperties(basename));

        return new CompositeResourceBundle(bundles);
    }

    protected List<URL> getResources(String fileName, String suffix) {
        return RESOURCE_LOCATOR.getResources(fileName + suffix);
    }

    protected URL getResourceAsURL(String fileName, String suffix) {
        return RESOURCE_LOCATOR.getResourceAsURL(fileName + suffix);
    }

    protected Class<?> loadClass(String className) throws ClassNotFoundException {
        return ApplicationClassLoader.get().loadClass(className);
    }

    protected Collection<ResourceBundle> loadBundleFromProperties(String fileName) {
        List<ResourceBundle> bundles = new ArrayList<ResourceBundle>();
        for (URL resource : getResources(fileName, PROPERTIES_SUFFIX)) {
            if (null == resource) continue;
            try {
                bundles.add(new PropertyResourceBundle(resource.openStream()));
            } catch (IOException e) {
                // ignore
            }
        }
        return bundles;
    }

    protected Collection<ResourceBundle> loadBundleFromScript(String fileName) {
        List<ResourceBundle> bundles = new ArrayList<ResourceBundle>();
        List<String> visitedUrls = new ArrayList<String>();
        for (URL resource : getResources(fileName, GROOVY_SUFFIX)) {
            if (null == resource) continue;
            bundles.add(new GroovyScriptResourceBundle(resource));
            visitedUrls.add(resource.toString());
        }
        URL resource = getResourceAsURL(fileName, CLASS_SUFFIX);
        if (null != resource) {
            String url = resource.toString().replace(CLASS_SUFFIX, GROOVY_SUFFIX);
            if (!visitedUrls.contains((url))) {
                String className = fileName.replace('/', '.');
                try {
                    Class klass = loadClass(className);
                    bundles.add(new GroovyScriptResourceBundle(klass));
                } catch (ClassNotFoundException cnfe) {
                    // should not happen!
                }
            }
        }
        return bundles;
    }
}
