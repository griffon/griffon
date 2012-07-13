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
import griffon.core.factories.ResourcesInjectorFactory;
import griffon.core.resources.ResourcesInjector;
import org.codehaus.griffon.runtime.core.resources.DefaultResourcesInjector;

/**
 * Default implementation of the {@code ResourcesInjectorFactory} interface.
 *
 * @author Andres Almiray
 * @since 1.1.0
 */
public class DefaultResourcesInjectorFactory implements ResourcesInjectorFactory {
    public ResourcesInjector create(GriffonApplication app) {
        return new DefaultResourcesInjector(app);
    }
}
