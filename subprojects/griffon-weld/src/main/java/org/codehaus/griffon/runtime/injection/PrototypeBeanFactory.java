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

import griffon.inject.Prototype;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.BeanManager;

/**
 * @author AndresAlmiray
 * @param <T>
 */
public class PrototypeBeanFactory<T> extends AbstractBeanFactory<T> {

    public PrototypeBeanFactory(BeanManager beanManager, Class<T> clazz, String name) {
        super(beanManager, clazz, name, Prototype.class);
    }

    public String toString() {
        return new StringBuilder("PrototypeBeanFactory[name=")
            .append(name)
            .append(", class=")
            .append(clazz.getName())
            .append(", annotatedType=")
            .append(annotatedType)
            .append(", scope=")
            .append(scope.getName())
            .append("]")
            .toString();
    }

    @Override
    protected T produce(CreationalContext<T> context) {
        return injectionTarget.produce(context);
    }
}