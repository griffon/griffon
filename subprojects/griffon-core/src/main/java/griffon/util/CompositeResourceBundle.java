/*
 * Copyright 2008-2015 the original author or authors.
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
package griffon.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static griffon.util.ExpandableResourceBundle.wrapResourceBundle;
import static griffon.util.GriffonClassUtils.requireState;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class CompositeResourceBundle extends ResourceBundle {
    private static final Logger LOG = LoggerFactory.getLogger(CompositeResourceBundle.class);
    private final ResourceBundle[] bundles;
    private final List<String> keys = new ArrayList<>();

    public CompositeResourceBundle(@Nonnull Collection<ResourceBundle> bundles) {
        this(toResourceBundleArray(bundles));
    }

    public CompositeResourceBundle(@Nonnull ResourceBundle[] bundles) {
        requireNonNull(bundles, "Argument 'bundles' must not be null");
        requireState(bundles.length > 0, "Argument 'bundles' must not be empty");
        this.bundles = new ResourceBundle[bundles.length];
        for (int i = 0; i < bundles.length; i++) {
            this.bundles[i] = wrapResourceBundle(bundles[i]);
        }

        for (ResourceBundle bundle : this.bundles) {
            Enumeration<String> ks = bundle.getKeys();
            while (ks.hasMoreElements()) {
                String key = ks.nextElement();
                if (!keys.contains(key)) {
                    keys.add(key);
                }
            }
        }
    }

    @Nullable
    protected Object handleGetObject(@Nonnull String key) {
        requireNonBlank(key, "Arguments 'key' must not be blank");

        LOG.trace("Searching key={}", key);
        for (ResourceBundle bundle : bundles) {
            try {
                Object value = bundle.getObject(key);
                LOG.trace("Bundle {}; key={}; value='{}'", bundle, key, value);
                if (value != null) {
                    return value;
                }
            } catch (Exception e) {
                // ignore
            }
        }
        return null;
    }

    @Nonnull
    @Override
    public Enumeration<String> getKeys() {
        return new IteratorAsEnumeration<>(keys.iterator());
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

    @Nonnull
    private static ResourceBundle[] toResourceBundleArray(@Nonnull Collection<ResourceBundle> bundles) {
        requireNonNull(bundles, "Argument 'bundles' must not be null");
        if (bundles.isEmpty()) {
            return new ResourceBundle[0];
        }
        return bundles.toArray(new ResourceBundle[bundles.size()]);
    }
}
