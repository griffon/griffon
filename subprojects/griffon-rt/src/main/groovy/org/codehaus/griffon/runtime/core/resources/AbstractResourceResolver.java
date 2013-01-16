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

package org.codehaus.griffon.runtime.core.resources;

import griffon.core.resources.NoSuchResourceException;
import griffon.core.resources.ResourceResolver;
import griffon.util.CallableWithArgs;
import groovy.lang.Closure;

import java.text.MessageFormat;
import java.util.*;

/**
 * @author Andres Almiray
 * @since 1.1.0
 */
public abstract class AbstractResourceResolver implements ResourceResolver {
    protected static final Object[] EMPTY_OBJECT_ARGS = new Object[0];

    public Object resolveResource(String key) throws NoSuchResourceException {
        return resolveResource(key, EMPTY_OBJECT_ARGS, Locale.getDefault());
    }

    public Object resolveResource(String key, Locale locale) throws NoSuchResourceException {
        return resolveResource(key, EMPTY_OBJECT_ARGS, locale);
    }

    public Object resolveResource(String key, Object[] args) throws NoSuchResourceException {
        return resolveResource(key, args, Locale.getDefault());
    }

    public Object resolveResource(String key, List args) throws NoSuchResourceException {
        return resolveResource(key, toObjectArray(args), Locale.getDefault());
    }

    public Object resolveResource(String key, List args, Locale locale) throws NoSuchResourceException {
        return resolveResource(key, toObjectArray(args), locale);
    }

    public Object resolveResource(String key, Object defaultValue) {
        return resolveResource(key, EMPTY_OBJECT_ARGS, defaultValue, Locale.getDefault());
    }

    public Object resolveResource(String key, Object defaultValue, Locale locale) {
        return resolveResource(key, EMPTY_OBJECT_ARGS, defaultValue, locale);
    }

    public Object resolveResource(String key, Object[] args, Object defaultValue) {
        return resolveResource(key, args, defaultValue, Locale.getDefault());
    }

    public Object resolveResource(String key, Object[] args, Object defaultValue, Locale locale) {
        try {
            return resolveResource(key, args, locale);
        } catch (NoSuchResourceException nsre) {
            return null == defaultValue ? key : defaultValue;
        }
    }

    public Object resolveResource(String key, Map<String, Object> args) throws NoSuchResourceException {
        return resolveResource(key, args, Locale.getDefault());
    }

    public Object resolveResource(String key, Map<String, Object> args, Object defaultValue) {
        return resolveResource(key, args, defaultValue, Locale.getDefault());
    }

    public Object resolveResource(String key, Map<String, Object> args, Object defaultValue, Locale locale) {
        try {
            return resolveResource(key, args, locale);
        } catch (NoSuchResourceException nsre) {
            return null == defaultValue ? key : defaultValue;
        }
    }

    public Object resolveResource(String key, Map<String, Object> args, Locale locale) throws NoSuchResourceException {
        if (null == args) args = Collections.emptyMap();
        if (null == locale) locale = Locale.getDefault();
        Object resource = resolveResourceValue(key, locale);
        Object result = evalResourceWithArguments(resource, args);
        if (result != null) {
            return result.toString();
        }
        throw new NoSuchResourceException(key, locale);
    }

    public Object resolveResource(String key, List args, Object defaultValue) {
        return resolveResource(key, toObjectArray(args), defaultValue, Locale.getDefault());
    }

    public Object resolveResource(String key, List args, Object defaultValue, Locale locale) {
        return resolveResource(key, toObjectArray(args), defaultValue, locale);
    }

    public Object resolveResource(String key, Object[] args, Locale locale) throws NoSuchResourceException {
        if (null == args) args = EMPTY_OBJECT_ARGS;
        if (null == locale) locale = Locale.getDefault();
        Object resource = resolveResourceValue(key, locale);
        Object result = evalResourceWithArguments(resource, args);
        if (result != null) {
            return result.toString();
        }
        throw new NoSuchResourceException(key, locale);
    }

    public Object resolveResourceValue(String key, Locale locale) throws NoSuchResourceException {
        try {
            Object resource = doResolveResourceValue(key, locale);
            if (resource instanceof CharSequence) {
                String msg = resource.toString();
                if (msg.length() >= 4 && msg.startsWith(REF_KEY_START) && msg.endsWith(REF_KEY_END)) {
                    String refKey = msg.substring(2, msg.length() - 1);
                    resource = resolveResourceValue(refKey, locale);
                }
            }
            return resource;
        } catch (MissingResourceException mre) {
            throw new NoSuchResourceException(key, locale);
        }
    }

    protected abstract Object doResolveResourceValue(String key, Locale locale) throws NoSuchResourceException;

    protected Object evalResourceWithArguments(Object resource, Object[] args) {
        if (resource instanceof Closure) {
            Closure closure = (Closure) resource;
            return closure.call(args);
        } else if (resource instanceof CallableWithArgs) {
            CallableWithArgs callable = (CallableWithArgs) resource;
            return callable.call(args);
        } else if (resource instanceof CharSequence) {
            return formatResource(String.valueOf(resource), args);
        }
        return null;
    }

    protected Object evalResourceWithArguments(Object resource, Map<String, Object> args) {
        if (resource instanceof Closure) {
            Closure closure = (Closure) resource;
            return closure.call(args);
        } else if (resource instanceof CallableWithArgs) {
            CallableWithArgs callable = (CallableWithArgs) resource;
            return callable.call(new Object[]{args});
        } else if (resource instanceof CharSequence) {
            return formatResource(String.valueOf(resource), args);
        }
        return null;
    }

    protected String formatResource(String resource, Object[] args) {
        return MessageFormat.format(resource, args);
    }

    protected String formatResource(String resource, Map<String, Object> args) {
        for (Map.Entry<String, Object> variable : args.entrySet()) {
            String var = variable.getKey();
            String value = variable.getValue() != null ? variable.getValue().toString() : null;
            if (value != null) {
                resource = resource.replace("{:" + var + "}", value);
            }
        }
        return resource;
    }

    protected Object[] toObjectArray(List args) {
        if (null == args || args.isEmpty()) {
            return EMPTY_OBJECT_ARGS;
        }
        return args.toArray(new Object[args.size()]);
    }
}
