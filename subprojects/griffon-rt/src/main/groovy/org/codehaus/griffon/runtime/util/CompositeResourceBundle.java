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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Andres Almiray
 * @since 1.1.0
 */
public class CompositeResourceBundle extends ResourceBundle {
    private static final Logger LOG = LoggerFactory.getLogger(CompositeResourceBundle.class);
    private final ResourceBundle[] bundles;
    private final List<String> keys = new ArrayList<String>();

    @Deprecated
    public static ResourceBundle create(String basename) {
        return create(basename, Locale.getDefault());
    }

    @Deprecated
    public static ResourceBundle create(String basename, Locale locale) {
        return new CompositeResourceBundleBuilder().create(basename, locale);
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
