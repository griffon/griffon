/*
 * Copyright 2008-2015 the original author or authors.
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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.Map;

/**
 * @author Andres Almiray
 * @since 2.4.0
 */
public class GriffonApplicationContext extends GenericApplicationContext {
    /**
     * Create a new GriffonApplicationContext.
     *
     * @see #registerBeanDefinition
     * @see #refresh
     */
    public GriffonApplicationContext() throws BeansException {
        super(new DefaultListableBeanFactory() {
            @Override
            protected String determineAutowireCandidate(Map<String, Object> candidateBeans, DependencyDescriptor descriptor) {
                String candidate = super.determineAutowireCandidate(candidateBeans, descriptor);
                if (candidate == null) {
                    String key = descriptor.getDependencyType().getName().toString();
                    if (candidateBeans.containsKey(key)) {
                        candidate = key;
                    }
                }
                return candidate;
            }
        });
    }

    /**
     * Create a new GriffonApplicationContext with the given parent.
     *
     * @see #registerBeanDefinition
     * @see #refresh
     */
    public GriffonApplicationContext(ApplicationContext parent) throws BeansException {
        super(parent);
    }

    /**
     * Overridden to turn it into a no-op, to be more lenient towards test cases.
     */
    @Override
    protected void assertBeanFactoryActive() {
    }
}
