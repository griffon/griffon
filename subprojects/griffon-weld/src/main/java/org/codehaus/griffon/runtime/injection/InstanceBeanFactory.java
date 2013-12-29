/*
 * Copyright 2011-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.griffon.runtime.injection;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Singleton;

/**
 * @author AndresAlmiray
 * @param <T>
 */
public class InstanceBeanFactory<T> extends AbstractBeanFactory<T> {
    private final T instance;

    public InstanceBeanFactory(BeanManager beanManager, Class<T> clazz, String name, T instance) {
        super(beanManager, clazz, name, Singleton.class);
        this.instance = instance;
    }

    public String toString() {
        return new StringBuilder("InstanceBeanFactory[name=")
            .append(name)
            .append(", class=")
            .append(clazz.getName())
            .append(", annotatedType=")
            .append(annotatedType)
            .append(", instance=")
            .append(instance)
            .append(", scope=")
            .append(scope.getName())
            .append("]")
            .toString();
    }

    @Override
    protected T produce(CreationalContext<T> context) {
        return instance;
    }
}