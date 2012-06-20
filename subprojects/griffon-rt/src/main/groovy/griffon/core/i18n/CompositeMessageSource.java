/*
 * Copyright 2010-2012 the original author or authors.
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

package griffon.core.i18n;

import java.util.List;
import java.util.Locale;

import static griffon.util.GriffonNameUtils.isBlank;

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

    public String getMessage(String key, String defaultMessage) {
        return getMessageInternal(key, EMPTY_OBJECT_ARGS, defaultMessage, Locale.getDefault());
    }

    public String getMessage(String key, String defaultMessage, Locale locale) {
        return getMessageInternal(key, EMPTY_OBJECT_ARGS, defaultMessage, locale);
    }

    public String getMessage(String key, Object[] args, String defaultMessage) {
        return getMessageInternal(key, args, defaultMessage, Locale.getDefault());
    }

    public String getMessage(String key, Object[] args, String defaultMessage, Locale locale) {
        return getMessageInternal(key, args, defaultMessage, locale);
    }

    public String getMessage(String key, List args, String defaultMessage) {
        return getMessageInternal(key, toObjectArray(args), defaultMessage, Locale.getDefault());
    }

    public String getMessage(String key, List args, String defaultMessage, Locale locale) {
        return getMessageInternal(key, toObjectArray(args), defaultMessage, locale);
    }

    public String resolveMessage(String key, Locale locale) {
        if (null == locale) locale = Locale.getDefault();
        for (MessageSource messageSource : messageSources) {
            try {
                if (messageSource instanceof AbstractMessageSource) {
                    return ((AbstractMessageSource) messageSource).resolveMessage(key, locale);
                }
                return messageSource.getMessage(key, locale);
            } catch (NoSuchMessageException nsme) {
                // ignore
            }
        }
        throw new NoSuchMessageException(key, locale);
    }

    private String getMessageInternal(String key, Object[] args, Locale locale) throws NoSuchMessageException {
        if (null == locale) locale = Locale.getDefault();
        for (MessageSource messageSource : messageSources) {
            try {
                return messageSource.getMessage(key, args, locale);
            } catch (NoSuchMessageException nsme) {
                // ignore
            }
        }
        throw new NoSuchMessageException(key, locale);
    }

    private String getMessageInternal(String key, Object[] args, String defaultMessage, Locale locale) {
        try {
            return getMessageInternal(key, args, locale);
        } catch (NoSuchMessageException nsme) {
            // ignore
        }
        return isBlank(defaultMessage) ? key : defaultMessage;
    }
}
