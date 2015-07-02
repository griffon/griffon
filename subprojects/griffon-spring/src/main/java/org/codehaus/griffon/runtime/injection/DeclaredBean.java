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

import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import javax.annotation.Nonnull;
import java.util.List;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * Code lifted from {@code juzu.impl.inject.spi.spring.DeclaredBean} original by Julien Viet.
 *
 * @author Andres Almiray
 * @since 2.4.0
 */
public class DeclaredBean<T> extends AbstractSpringBean<T> {
    private static final String ERROR_SCOPE_BLANK = "Argument 'scope' must not be blank";
    private final String scope;
    private final String alias;
    private Class<? super T> target;

    public DeclaredBean(
        @Nonnull Class<T> source,
        @Nonnull Class<? super T> target,
        @Nonnull String scope,
        @Nonnull List<AutowireCandidateQualifier> qualifiers) {
        super(source, qualifiers);
        this.target = requireNonNull(target, "Argument 'target' must not be null");
        this.scope = requireNonBlank(scope, ERROR_SCOPE_BLANK);
        this.alias = BeanUtils.Key.of(target, qualifiers).asFormattedName();
    }

    @Nonnull
    public Class<?> getType() {
        return target;
    }


    @Override
    public void configure(@Nonnull String name, @Nonnull SpringContext context, @Nonnull DefaultListableBeanFactory factory) {
        AnnotatedGenericBeanDefinition definition = new AnnotatedGenericBeanDefinition(getType());
        definition.setScope(scope);

        for (AutowireCandidateQualifier qualifier : getQualifiers()) {
            definition.addQualifier(qualifier);
        }

        factory.registerBeanDefinition(name, definition);
        factory.registerAlias(name, alias);
    }
}
