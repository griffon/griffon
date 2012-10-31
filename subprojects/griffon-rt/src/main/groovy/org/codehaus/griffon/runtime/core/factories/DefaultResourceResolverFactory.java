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
import griffon.core.factories.ResourceResolverFactory;
import griffon.core.resources.ResourceResolver;
import org.codehaus.griffon.runtime.core.resources.CompositeResourceResolver;
import org.codehaus.griffon.runtime.core.resources.DefaultResourceResolver;

import java.util.ArrayList;
import java.util.List;

import static griffon.util.ConfigUtils.getConfigValue;
import static java.util.Arrays.asList;

/**
 * Default implementation of the {@code ResourceResolverFactory} interface.
 *
 * @author Andres Almiray
 * @since 1.1.0
 */
public class DefaultResourceResolverFactory implements ResourceResolverFactory {
    public ResourceResolver create(GriffonApplication app) {
        List<String> basenames = (List<String>) getConfigValue(app.getConfig(), "resources.basenames", asList("resources"));
        List<ResourceResolver> resourceResolvers = new ArrayList<ResourceResolver>();
        for (String basename : basenames) {
            resourceResolvers.add(new DefaultResourceResolver(basename));
        }

        return new CompositeResourceResolver(resourceResolvers);
    }
}
