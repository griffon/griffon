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

package org.codehaus.griffon.runtime.core.i18n;

import griffon.core.i18n.MessageSource;
import griffon.core.i18n.NoSuchMessageException;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 1.1.0
 */
public class CompositeMessageSource extends AbstractMessageSource {
    private final MessageSource[] messageSources;

    public CompositeMessageSource(@Nonnull List<MessageSource> messageSources) {
        this(toMessageSourceArray(messageSources));
    }

    public CompositeMessageSource(@Nonnull MessageSource[] messageSources) {
        this.messageSources = requireNonNull(messageSources, "Argument 'messageSources' cannot be null");
    }

    private static MessageSource[] toMessageSourceArray(@Nonnull List<MessageSource> messageSources) {
        requireNonNull(messageSources, "Argument 'messageSources' cannot be null");
        if (messageSources.isEmpty()) {
            return new MessageSource[0];
        }
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
}
