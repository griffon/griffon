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

import griffon.util.CompositeResourceBundle;
import griffon.util.CompositeResourceBundleBuilder;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractCompositeResourceBundleBuilder implements CompositeResourceBundleBuilder {
    protected static final String ERROR_BASENAME_BLANK = "Argument 'basename' must not be blank";
    protected static final String ERROR_LOCALE_NULL = "Argument 'locale' must not be null";

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

        initialize();

        String[] combinations = {
            locale.getLanguage() + "_" + locale.getCountry() + "_" + locale.getVariant(),
            locale.getLanguage() + "_" + locale.getCountry(),
            locale.getLanguage()
        };

        basename = basename.replace('.', '/');
        List<ResourceBundle> bundles = new ArrayList<>();
        for (String suffix : combinations) {
            if (suffix.endsWith("_")) { continue; }
            bundles.addAll(loadBundlesFor(basename + "_" + suffix));
        }
        bundles.addAll(loadBundlesFor(basename));
        if (bundles.isEmpty()) {
            throw new IllegalArgumentException("There are no ResourceBundle resources matching " + basename);
        }

        return new CompositeResourceBundle(bundles);
    }

    protected abstract void initialize();

    @Nonnull
    protected abstract Collection<ResourceBundle> loadBundlesFor(@Nonnull String basename);
}
