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

package org.codehaus.griffon.runtime.core.i18n;

import griffon.core.i18n.MessageSource;
import griffon.core.i18n.NoSuchMessageException;

import java.util.List;
import java.util.Locale;

/**
 * @author Andres Almiray
 * @since 1.1.0
 */
public class CompositeMessageSource extends AbstractMessageSource {
    private final MessageSource[] messageSources;

    public CompositeMessageSource(List<MessageSource> messageSources) {
        this(toMessageSourceArray(messageSources));
    }

    public CompositeMessageSource(MessageSource[] messageSources) {
        this.messageSources = messageSources;
    }

    private static MessageSource[] toMessageSourceArray(List<MessageSource> messageSources) {
        if (null == messageSources || messageSources.isEmpty()) {
            return new MessageSource[0];
        }
        return messageSources.toArray(new MessageSource[messageSources.size()]);
    }

    @Override
    protected Object doResolveMessageValue(String key, Locale locale) throws NoSuchMessageException {
        if (null == locale) locale = Locale.getDefault();
        for (MessageSource messageSource : messageSources) {
            try {
                return messageSource.resolveMessageValue(key, locale);
            } catch (NoSuchMessageException nsme) {
                // ignore
            }
        }
        throw new NoSuchMessageException(key, locale);
    }
}
