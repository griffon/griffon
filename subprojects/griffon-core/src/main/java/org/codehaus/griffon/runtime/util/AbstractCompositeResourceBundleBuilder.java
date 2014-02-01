/*
 * Copyright 2008-2014 the original author or authors.
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
import griffon.util.CompositeResourceBundle;
import griffon.util.CompositeResourceBundleBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.net.URL;
import java.util.*;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractCompositeResourceBundleBuilder implements CompositeResourceBundleBuilder {
    protected static final String ERROR_FILENAME_BLANK = "Argument 'fileName' cannot be blank";
    protected static final String ERROR_SUFFIX_BLANK = "Argument 'suffix' cannot be blank";
    protected static final String ERROR_RESOURCE_HANDLER_NULL = "Argument 'resourceHandler' cannot be null";
    protected static final String ERROR_BASENAME_BLANK = "Argument 'basename' cannot be blank";
    protected static final String ERROR_LOCALE_NULL = "Argument 'locale' cannot be null";

    protected final ResourceHandler resourceHandler;

    @Inject
    public AbstractCompositeResourceBundleBuilder(@Nonnull ResourceHandler resourceHandler) {
        this.resourceHandler = requireNonNull(resourceHandler, ERROR_RESOURCE_HANDLER_NULL);
    }

    @Override
    @Nonnull
    public ResourceBundle create(@Nonnull String basename) {
        return create(basename, Locale.getDefault());
    }

    @Override
    @Nonnull
    public ResourceBundle create(@Nonnull String basename, @Nonnull Locale locale) {
        requireNonBlank(basename, ERROR_BASENAME_BLANK);
        requireNonNull(locale, ERROR_LOCALE_NULL);

        String[] combinations = {
            locale.getLanguage() + "_" + locale.getCountry() + "_" + locale.getVariant(),
            locale.getLanguage() + "_" + locale.getCountry(),
            locale.getLanguage()
        };

        basename = basename.replace('.', '/');
        List<ResourceBundle> bundles = new ArrayList<>();
        for (String suffix : combinations) {
            if (suffix.endsWith("_")) continue;
            bundles.addAll(loadBundlesFor(basename + "_" + suffix));
        }
        bundles.addAll(loadBundlesFor(basename));
        if (bundles.size() == 0) {
            throw new IllegalArgumentException("There are no ResourceBundle resources matching " + basename);
        }

        return new CompositeResourceBundle(bundles);
    }

    @Nonnull
    protected ResourceHandler getResourceHandler() {
        return resourceHandler;
    }

    @Nullable
    protected URL getResourceAsURL(@Nonnull String fileName, @Nonnull String suffix) {
        requireNonBlank(fileName, ERROR_FILENAME_BLANK);
        requireNonBlank(suffix, ERROR_SUFFIX_BLANK);
        return resourceHandler.getResourceAsURL(fileName + suffix);
    }

    @Nullable
    protected List<URL> getResources(@Nonnull String fileName, @Nonnull String suffix) {
        requireNonBlank(fileName, ERROR_FILENAME_BLANK);
        requireNonBlank(suffix, ERROR_SUFFIX_BLANK);
        return resourceHandler.getResources(fileName + suffix);
    }

    @Nonnull
    protected abstract Collection<ResourceBundle> loadBundlesFor(@Nonnull String basename);
}
