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

import griffon.util.CallableWithArgs;
import groovy.lang.DelegatingMetaClass;
import groovy.lang.ExpandoMetaClass;
import groovy.lang.MetaClass;
import org.codehaus.griffon.runtime.util.CallableWithArgsMetaMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Andres Almiray
 * @since 1.1.0
 */
public final class I18nEnhancer {
    private static final Logger LOG = LoggerFactory.getLogger(I18nEnhancer.class);

    private I18nEnhancer() {
    }

    public static void enhance(MetaClass mc) {
        enhance(mc, MessageSourceHolder.getInstance());
    }

    public static void enhance(MetaClass mc, MessageSourceProvider provider) {
        if (null == mc) return;

        ExpandoMetaClass emc = null;
        if (mc instanceof DelegatingMetaClass) {
            mc = ((DelegatingMetaClass) mc).getAdaptee();
        }
        if (mc instanceof ExpandoMetaClass) {
            emc = (ExpandoMetaClass) mc;
        }
        if (null == emc) return;
        if (null == provider) provider = MessageSourceHolder.getInstance();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Enhancing " + mc + " with " + provider);
        }

        final MessageSourceProvider messageSourceProvider = provider;

        emc.registerInstanceMethod(
                new MessageSourceMetaMethod(
                        emc.getTheClass(),
                        new Class[]{String.class},
                        new CallableWithArgs<String>() {
                            public String call(Object[] args) {
                                String key = (String) args[0];
                                return messageSourceProvider.getMessageSource().getMessage(key);
                            }
                        }));
        emc.registerInstanceMethod(
                new MessageSourceMetaMethod(
                        emc.getTheClass(),
                        new Class[]{String.class, Locale.class},
                        new CallableWithArgs<String>() {
                            public String call(Object[] args) {
                                String key = (String) args[0];
                                Locale locale = (Locale) args[1];
                                return messageSourceProvider.getMessageSource().getMessage(key, locale);
                            }
                        }));
        emc.registerInstanceMethod(
                new MessageSourceMetaMethod(
                        emc.getTheClass(),
                        new Class[]{String.class, Object[].class},
                        new CallableWithArgs<String>() {
                            public String call(Object[] args) {
                                String key = (String) args[0];
                                Object[] params = (Object[]) args[1];
                                return messageSourceProvider.getMessageSource().getMessage(key, params);
                            }
                        }));
        emc.registerInstanceMethod(
                new MessageSourceMetaMethod(
                        emc.getTheClass(),
                        new Class[]{String.class, Object[].class, Locale.class},
                        new CallableWithArgs<String>() {
                            public String call(Object[] args) {
                                String key = (String) args[0];
                                Object[] params = (Object[]) args[1];
                                Locale locale = (Locale) args[2];
                                return messageSourceProvider.getMessageSource().getMessage(key, params, locale);
                            }
                        }));
        emc.registerInstanceMethod(
                new MessageSourceMetaMethod(
                        emc.getTheClass(),
                        new Class[]{String.class, List.class},
                        new CallableWithArgs<String>() {
                            public String call(Object[] args) {
                                String key = (String) args[0];
                                List params = (List) args[1];
                                return messageSourceProvider.getMessageSource().getMessage(key, params);
                            }
                        }));
        emc.registerInstanceMethod(
                new MessageSourceMetaMethod(
                        emc.getTheClass(),
                        new Class[]{String.class, List.class, Locale.class},
                        new CallableWithArgs<String>() {
                            public String call(Object[] args) {
                                String key = (String) args[0];
                                List params = (List) args[1];
                                Locale locale = (Locale) args[2];
                                return messageSourceProvider.getMessageSource().getMessage(key, params, locale);
                            }
                        }));
        emc.registerInstanceMethod(
                new MessageSourceMetaMethod(
                        emc.getTheClass(),
                        new Class[]{String.class, Map.class},
                        new CallableWithArgs<String>() {
                            public String call(Object[] args) {
                                String key = (String) args[0];
                                Map params = (Map) args[1];
                                return messageSourceProvider.getMessageSource().getMessage(key, params);
                            }
                        }));
        emc.registerInstanceMethod(
                new MessageSourceMetaMethod(
                        emc.getTheClass(),
                        new Class[]{String.class, Map.class, Locale.class},
                        new CallableWithArgs<String>() {
                            public String call(Object[] args) {
                                String key = (String) args[0];
                                Map params = (Map) args[1];
                                Locale locale = (Locale) args[2];
                                return messageSourceProvider.getMessageSource().getMessage(key, params, locale);
                            }
                        }));

        emc.registerInstanceMethod(
                new MessageSourceMetaMethod(
                        emc.getTheClass(),
                        new Class[]{String.class, String.class},
                        new CallableWithArgs<String>() {
                            public String call(Object[] args) {
                                String key = (String) args[0];
                                String defaultMessage = (String) args[1];
                                return messageSourceProvider.getMessageSource().getMessage(key, defaultMessage);
                            }
                        }));
        emc.registerInstanceMethod(
                new MessageSourceMetaMethod(
                        emc.getTheClass(),
                        new Class[]{String.class, String.class, Locale.class},
                        new CallableWithArgs<String>() {
                            public String call(Object[] args) {
                                String key = (String) args[0];
                                String defaultMessage = (String) args[1];
                                Locale locale = (Locale) args[2];
                                return messageSourceProvider.getMessageSource().getMessage(key, defaultMessage, locale);
                            }
                        }));
        emc.registerInstanceMethod(
                new MessageSourceMetaMethod(
                        emc.getTheClass(),
                        new Class[]{String.class, Object[].class, String.class},
                        new CallableWithArgs<String>() {
                            public String call(Object[] args) {
                                String key = (String) args[0];
                                Object[] params = (Object[]) args[1];
                                String defaultMessage = (String) args[2];
                                return messageSourceProvider.getMessageSource().getMessage(key, params, defaultMessage);
                            }
                        }));
        emc.registerInstanceMethod(
                new MessageSourceMetaMethod(
                        emc.getTheClass(),
                        new Class[]{String.class, Object[].class, String.class, Locale.class},
                        new CallableWithArgs<String>() {
                            public String call(Object[] args) {
                                String key = (String) args[0];
                                Object[] params = (Object[]) args[1];
                                String defaultMessage = (String) args[2];
                                Locale locale = (Locale) args[3];
                                return messageSourceProvider.getMessageSource().getMessage(key, params, defaultMessage, locale);
                            }
                        }));
        emc.registerInstanceMethod(
                new MessageSourceMetaMethod(
                        emc.getTheClass(),
                        new Class[]{String.class, List.class, String.class},
                        new CallableWithArgs<String>() {
                            public String call(Object[] args) {
                                String key = (String) args[0];
                                List params = (List) args[1];
                                String defaultMessage = (String) args[2];
                                return messageSourceProvider.getMessageSource().getMessage(key, params, defaultMessage);
                            }
                        }));
        emc.registerInstanceMethod(
                new MessageSourceMetaMethod(
                        emc.getTheClass(),
                        new Class[]{String.class, List.class, String.class, Locale.class},
                        new CallableWithArgs<String>() {
                            public String call(Object[] args) {
                                String key = (String) args[0];
                                List params = (List) args[1];
                                String defaultMessage = (String) args[2];
                                Locale locale = (Locale) args[3];
                                return messageSourceProvider.getMessageSource().getMessage(key, params, defaultMessage, locale);
                            }
                        }));
        emc.registerInstanceMethod(
                new MessageSourceMetaMethod(
                        emc.getTheClass(),
                        new Class[]{String.class, Map.class, String.class},
                        new CallableWithArgs<String>() {
                            public String call(Object[] args) {
                                String key = (String) args[0];
                                Map params = (Map) args[1];
                                String defaultMessage = (String) args[2];
                                return messageSourceProvider.getMessageSource().getMessage(key, params, defaultMessage);
                            }
                        }));
        emc.registerInstanceMethod(
                new MessageSourceMetaMethod(
                        emc.getTheClass(),
                        new Class[]{String.class, Map.class, String.class, Locale.class},
                        new CallableWithArgs<String>() {
                            public String call(Object[] args) {
                                String key = (String) args[0];
                                Map params = (Map) args[1];
                                String defaultMessage = (String) args[2];
                                Locale locale = (Locale) args[3];
                                return messageSourceProvider.getMessageSource().getMessage(key, params, defaultMessage, locale);
                            }
                        }));
    }

    private static class MessageSourceMetaMethod extends CallableWithArgsMetaMethod {
        private MessageSourceMetaMethod(Class declaringClass, Class[] parameterTypes, CallableWithArgs runnable) {
            super("getMessage", declaringClass, runnable, parameterTypes);
        }
    }
}