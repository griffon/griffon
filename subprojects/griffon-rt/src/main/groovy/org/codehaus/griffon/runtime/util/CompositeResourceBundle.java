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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static griffon.util.GriffonNameUtils.isBlank;

/**
 * @author Andres Almiray
 * @since 1.1.0
 */
public class CompositeResourceBundle extends ResourceBundle {
    private static final Logger LOG = LoggerFactory.getLogger(CompositeResourceBundle.class);
    private final ResourceBundle[] bundles;
    private final List<String> keys = new ArrayList<String>();
    private static final ResourceLocator RESOURCE_LOCATOR = new ResourceLocator();

    public static ResourceBundle create(String basename) {
        return create(basename, Locale.getDefault());
    }

    public static ResourceBundle create(String basename, Locale locale) {
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
            bundles.addAll(loadBundleFromProperties(basename + "_" + suffix));
            bundles.addAll(loadBundleFromScript(basename + "_" + suffix));
        }
        bundles.addAll(loadBundleFromProperties(basename));
        bundles.addAll(loadBundleFromScript(basename));

        return new CompositeResourceBundle(bundles);
    }

    private static Collection<ResourceBundle> loadBundleFromProperties(String fileName) {
        List<ResourceBundle> bundles = new ArrayList<ResourceBundle>();
        for (URL resource : RESOURCE_LOCATOR.getResources(fileName + ".properties")) {
            if (null == resource) continue;
            try {
                bundles.add(new PropertyResourceBundle(resource.openStream()));
            } catch (IOException e) {
                // ignore
            }
        }
        return bundles;
    }

    private static Collection<ResourceBundle> loadBundleFromScript(String fileName) {
        List<ResourceBundle> bundles = new ArrayList<ResourceBundle>();
        List<String> visitedUrls = new ArrayList<String>();
        for (URL resource : RESOURCE_LOCATOR.getResources(fileName + ".groovy")) {
            if (null == resource) continue;
            bundles.add(new GroovyScriptResourceBundle(resource));
            visitedUrls.add(resource.toString());
        }
        URL resource = RESOURCE_LOCATOR.getResourceAsURL(fileName + ".class");
        if (null != resource) {
            String url = resource.toString().replace(".class", ".groovy");
            if (!visitedUrls.contains((url))) {
                String className = fileName.replace('/', '.');
                try {
                    Class klass = ApplicationClassLoader.get().loadClass(className);
                    bundles.add(new GroovyScriptResourceBundle(klass));
                } catch (ClassNotFoundException cnfe) {
                    // should not happen!
                }
            }
        }
        return bundles;
    }

    public CompositeResourceBundle(List<ResourceBundle> bundles) {
        this(toResourceBundleArray(bundles));
    }

    public CompositeResourceBundle(ResourceBundle[] bundles) {
        this.bundles = bundles;
        for (ResourceBundle bundle : bundles) {
            Enumeration<String> ks = bundle.getKeys();
            while (ks.hasMoreElements()) {
                String key = ks.nextElement();
                if (!keys.contains(key)) {
                    keys.add(key);
                }
            }
        }
    }

    protected Object handleGetObject(String key) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Searching key=" + key);
        }
        for (ResourceBundle bundle : bundles) {
            try {
                Object value = bundle.getObject(key);
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Bundle " + bundle + "; key=" + key + "; value='" + value + "'");
                }
                if (value != null) {
                    return value;
                }
            } catch (Exception e) {
                // ignore
            }
        }
        return null;
    }

    @Override
    public Enumeration<String> getKeys() {
        return new IteratorAsEnumeration<String>(keys.iterator());
    }

    private static class IteratorAsEnumeration<E> implements Enumeration<E> {
        private final Iterator<E> iterator;

        public IteratorAsEnumeration(Iterator<E> iterator) {
            this.iterator = iterator;
        }

        public boolean hasMoreElements() {
            return iterator.hasNext();
        }

        public E nextElement() {
            return iterator.next();
        }
    }

    private static ResourceBundle[] toResourceBundleArray(List<ResourceBundle> bundles) {
        if (null == bundles || bundles.isEmpty()) {
            return new ResourceBundle[0];
        }
        return bundles.toArray(new ResourceBundle[bundles.size()]);
    }
}
