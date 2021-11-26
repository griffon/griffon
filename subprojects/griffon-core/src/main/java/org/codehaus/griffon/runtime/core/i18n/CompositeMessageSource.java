/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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
package org.codehaus.griffon.runtime.core.i18n;

import griffon.core.i18n.MessageSource;
import griffon.core.i18n.NoSuchMessageException;
import griffon.util.CompositeResourceBundle;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static griffon.util.GriffonClassUtils.requireNonEmpty;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class CompositeMessageSource extends AbstractMessageSource {
    private final MessageSource[] messageSources;

    public CompositeMessageSource(@Nonnull Collection<MessageSource> messageSources) {
        this(toMessageSourceArray(messageSources));
    }

    public CompositeMessageSource(@Nonnull MessageSource[] messageSources) {
        this.messageSources = requireNonNull(messageSources, "Argument 'messageSources' must not be null");
    }

    private static MessageSource[] toMessageSourceArray(@Nonnull Collection<MessageSource> messageSources) {
        requireNonNull(messageSources, "Argument 'messageSources' must not be null");
        requireNonEmpty(messageSources, "Argument 'messageSources' must not be empty");
        return messageSources.toArray(new MessageSource[messageSources.size()]);
    }

    @Nonnull
    @Override
    protected Object doResolveMessageValue(@Nonnull String key, @Nonnull Locale locale) throws NoSuchMessageException {
        requireNonBlank(key, ERROR_KEY_BLANK);
        requireNonNull(locale, ERROR_LOCALE_NULL);
        for (MessageSource messageSource : messageSources) {
            try {
                return messageSource.getMessage(key, locale);
            } catch (NoSuchMessageException nsme) {
                // ignore
            }
        }
        throw new NoSuchMessageException(key, locale);
    }

    @Nonnull
    @Override
    public ResourceBundle asResourceBundle() {
        List<ResourceBundle> bundles = new ArrayList<>();
        for (MessageSource messageSource : messageSources) {
            bundles.add(messageSource.asResourceBundle());
        }
        return new CompositeResourceBundle(bundles);
    }
}
