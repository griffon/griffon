/*
 * Copyright 2009-2012 the original author or authors.
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

package org.codehaus.griffon.runtime.core.factories;

import griffon.core.GriffonApplication;
import org.codehaus.griffon.runtime.core.i18n.CompositeMessageSource;
import org.codehaus.griffon.runtime.core.i18n.DefaultMessageSource;
import griffon.core.i18n.MessageSource;
import griffon.core.factories.MessageSourceFactory;

import java.util.ArrayList;
import java.util.List;

import static griffon.util.ConfigUtils.getConfigValue;
import static java.util.Arrays.asList;

/**
 * Default implementation of the {@code MessageSourceFactory} interface.
 *
 * @author Andres Almiray
 * @since 1.1.0
 */
public class DefaultMessageSourceFactory implements MessageSourceFactory {
    public MessageSource create(GriffonApplication app) {
        List<String> i18nBasenames = (List<String>) getConfigValue(app.getConfig(), "i18n.basenames", asList("messages"));
        List<MessageSource> messageSources = new ArrayList<MessageSource>();
        for (String basename : i18nBasenames) {
            messageSources.add(new DefaultMessageSource(basename));
        }

        return new CompositeMessageSource(messageSources);
    }
}
