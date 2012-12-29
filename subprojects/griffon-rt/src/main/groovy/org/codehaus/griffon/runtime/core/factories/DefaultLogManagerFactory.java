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
import griffon.core.factories.LogManagerFactory;
import griffon.util.logging.LogManager;
import org.codehaus.griffon.runtime.logging.DefaultLogManager;

/**
 * Default implementation of the {@code LogManagerFactory} interface.
 *
 * @author Andres Almiray
 * @since 1.2.0
 */
public class DefaultLogManagerFactory implements LogManagerFactory {
    public LogManager create(GriffonApplication app) {
        return new DefaultLogManager(app);
    }
}
