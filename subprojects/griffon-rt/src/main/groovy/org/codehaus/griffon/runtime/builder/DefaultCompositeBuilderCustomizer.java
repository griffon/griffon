/*
 * Copyright 2008-2014 the original author or authors.
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
package org.codehaus.griffon.runtime.builder;

import groovy.lang.Closure;
import groovy.util.Factory;
import groovy.util.FactoryBuilderSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of the <code>CompositeBuilderCustomizer</code> interface.
 *
 * @author Andres Almiray
 * @since 0.9.3
 */
public class DefaultCompositeBuilderCustomizer implements CompositeBuilderCustomizer {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultCompositeBuilderCustomizer.class);

    public void registerFactory(FactoryBuilderSupport builder, String name, String groupName, Factory factory) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Registering factory " + groupName + ":" + name + " with " + factory);
        }
        builder.registerFactory(name, groupName, factory);
    }

    public void registerBeanFactory(FactoryBuilderSupport builder, String name, String groupName, Class<?> beanClass) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Registering factory " + groupName + ":" + name + " with " + beanClass.getName());
        }
        builder.registerBeanFactory(name, groupName, beanClass);
    }

    public void registerExplicitMethod(FactoryBuilderSupport builder, String name, String groupName, Closure method) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Registering method " + groupName + ":" + name);
        }
        builder.registerExplicitMethod(name, groupName, method);
    }

    public void registerExplicitProperty(FactoryBuilderSupport builder, String name, String groupName, Closure getter, Closure setter) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Registering property " + groupName + ":" + name);
        }
        builder.registerExplicitProperty(name, groupName, getter, setter);
    }
}
