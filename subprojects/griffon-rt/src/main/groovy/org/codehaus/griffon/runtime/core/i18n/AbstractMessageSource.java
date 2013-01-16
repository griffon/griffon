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
import griffon.util.CallableWithArgs;
import groovy.lang.Closure;

import java.text.MessageFormat;
import java.util.*;

/**
 * @author Andres Almiray
 * @author Alexander Klein
 * @since 1.1.0
 */
public abstract class AbstractMessageSource implements MessageSource {
    protected static final Object[] EMPTY_OBJECT_ARGS = new Object[0];

    public String getMessage(String key) throws NoSuchMessageException {
        return getMessage(key, EMPTY_OBJECT_ARGS, Locale.getDefault());
    }

    public String getMessage(String key, Locale locale) throws NoSuchMessageException {
        return getMessage(key, EMPTY_OBJECT_ARGS, locale);
    }

    public String getMessage(String key, Object[] args) throws NoSuchMessageException {
        return getMessage(key, args, Locale.getDefault());
    }

    public String getMessage(String key, List args) throws NoSuchMessageException {
        return getMessage(key, toObjectArray(args), Locale.getDefault());
    }

    public String getMessage(String key, List args, Locale locale) throws NoSuchMessageException {
        return getMessage(key, toObjectArray(args), locale);
    }

    public String getMessage(String key, String defaultMessage) {
        return getMessage(key, EMPTY_OBJECT_ARGS, defaultMessage, Locale.getDefault());
    }

    public String getMessage(String key, String defaultMessage, Locale locale) {
        return getMessage(key, EMPTY_OBJECT_ARGS, defaultMessage, locale);
    }

    public String getMessage(String key, Object[] args, String defaultMessage) {
        return getMessage(key, args, defaultMessage, Locale.getDefault());
    }

    public String getMessage(String key, Object[] args, String defaultMessage, Locale locale) {
        try {
            return getMessage(key, args, locale);
        } catch (NoSuchMessageException nsme) {
            return null == defaultMessage ? key : defaultMessage;
        }
    }

    public String getMessage(String key, Map<String, Object> args) throws NoSuchMessageException {
        return getMessage(key, args, Locale.getDefault());
    }

    public String getMessage(String key, Map<String, Object> args, String defaultMessage) {
        return getMessage(key, args, defaultMessage, Locale.getDefault());
    }

    public String getMessage(String key, Map<String, Object> args, String defaultMessage, Locale locale) {
        try {
            return getMessage(key, args, locale);
        } catch (NoSuchMessageException nsme) {
            return null == defaultMessage ? key : defaultMessage;
        }
    }

    public String getMessage(String key, Map<String, Object> args, Locale locale) throws NoSuchMessageException {
        if (null == args) args = Collections.emptyMap();
        if (null == locale) locale = Locale.getDefault();
        Object message = resolveMessageValue(key, locale);
        Object result = evalMessageWithArguments(message, args);
        if (result != null) {
            return result.toString();
        }
        throw new NoSuchMessageException(key, locale);
    }

    public String getMessage(String key, List args, String defaultMessage) {
        return getMessage(key, toObjectArray(args), defaultMessage, Locale.getDefault());
    }

    public String getMessage(String key, List args, String defaultMessage, Locale locale) {
        return getMessage(key, toObjectArray(args), defaultMessage, locale);
    }

    public String getMessage(String key, Object[] args, Locale locale) throws NoSuchMessageException {
        if (null == args) args = EMPTY_OBJECT_ARGS;
        if (null == locale) locale = Locale.getDefault();
        Object message = resolveMessageValue(key, locale);
        Object result = evalMessageWithArguments(message, args);
        if (result != null) {
            return result.toString();
        }
        throw new NoSuchMessageException(key, locale);
    }

    public Object resolveMessageValue(String key, Locale locale) throws NoSuchMessageException {
        try {
            Object message = doResolveMessageValue(key, locale);
            if (message instanceof CharSequence) {
                String msg = message.toString();
                if (msg.length() >= 4 && msg.startsWith(REF_KEY_START) && msg.endsWith(REF_KEY_END)) {
                    String refKey = msg.substring(2, msg.length() - 1);
                    message = resolveMessageValue(refKey, locale);
                }
            }
            return message;
        } catch (MissingResourceException mre) {
            throw new NoSuchMessageException(key, locale);
        }
    }

    protected abstract Object doResolveMessageValue(String key, Locale locale) throws NoSuchMessageException;

    protected Object evalMessageWithArguments(Object message, Object[] args) {
        if (message instanceof Closure) {
            Closure closure = (Closure) message;
            return closure.call(args);
        } else if (message instanceof CallableWithArgs) {
            CallableWithArgs callable = (CallableWithArgs) message;
            return callable.call(args);
        } else if (message instanceof CharSequence) {
            return formatMessage(String.valueOf(message), args);
        }
        return null;
    }

    protected Object evalMessageWithArguments(Object message, Map<String, Object> args) {
        if (message instanceof Closure) {
            Closure closure = (Closure) message;
            return closure.call(args);
        } else if (message instanceof CallableWithArgs) {
            CallableWithArgs callable = (CallableWithArgs) message;
            return callable.call(new Object[]{args});
        } else if (message instanceof CharSequence) {
            return formatMessage(String.valueOf(message), args);
        }
        return null;
    }

    protected String formatMessage(String message, Object[] args) {
        return MessageFormat.format(message, args);
    }

    protected String formatMessage(String message, Map<String, Object> args) {
        for (Map.Entry<String, Object> variable : args.entrySet()) {
            String var = variable.getKey();
            String value = variable.getValue() != null ? variable.getValue().toString() : null;
            if (value != null) {
                message = message.replace("{:" + var + "}", value);
            }
        }
        return message;
    }

    protected Object[] toObjectArray(List args) {
        if (null == args || args.isEmpty()) {
            return EMPTY_OBJECT_ARGS;
        }
        return args.toArray(new Object[args.size()]);
    }
}
