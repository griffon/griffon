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
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Map;

import static java.util.Arrays.copyOf;

/**
 * @author Andres Almiray
 * @since 2.4.0
 */
public class GriffonApplicationContext extends AnnotationConfigApplicationContext {
    private String[] basePackages;
    private Class[] annotatedClasses;

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
     * Create a new AnnotationConfigApplicationContext, deriving bean definitions
     * from the given annotated classes and automatically refreshing the context.
     *
     * @param annotatedClasses one or more annotated classes,
     *                         e.g. {@link org.springframework.context.annotation.Configuration @Configuration} classes
     */
    public GriffonApplicationContext(Class<?>... annotatedClasses) {
        this();
        this.annotatedClasses = copyOf(annotatedClasses, annotatedClasses.length);
    }

    /**
     * Create a new AnnotationConfigApplicationContext, scanning for bean definitions
     * in the given packages and automatically refreshing the context.
     *
     * @param basePackages the packages to check for annotated classes
     */
    public GriffonApplicationContext(String... basePackages) {
        this();
        this.basePackages = copyOf(basePackages, basePackages.length);
    }

    public void init() {
        if (basePackages != null) {
            scan(basePackages);
        } else {
            register(annotatedClasses);
        }
        refresh();
    }
}