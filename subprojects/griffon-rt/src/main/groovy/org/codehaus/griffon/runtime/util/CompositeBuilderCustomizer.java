/*
 * Copyright 2008-2012 the original author or authors.
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
package org.codehaus.griffon.runtime.util;

import groovy.lang.Closure;
import groovy.util.Factory;
import org.codehaus.griffon.runtime.builder.UberBuilder;

/**
 * Defines the contract for toolkit specific configurations that can be applied to an <code>UberBuilder</code> instance.
 *
 * @author Andres Almiray
 * @since 0.9.3
 */
public interface CompositeBuilderCustomizer {
    void registerFactory(UberBuilder uberBuilder, String name, String groupName, Factory factory);

    void registerBeanFactory(UberBuilder uberBuilder, String name, String groupName, Class<?> beanClass);

    void registerExplicitMethod(UberBuilder uberBuilder, String name, String groupName, Closure method);

    void registerExplicitProperty(UberBuilder uberBuilder, String name, String groupName, Closure getter, Closure setter);
}
