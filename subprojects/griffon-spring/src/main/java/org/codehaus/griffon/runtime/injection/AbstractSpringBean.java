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

import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Code lifted from {@code juzu.impl.inject.spi.spring.AbstractBean} original from Julien Viet.
 *
 * @author Andres Almiray
 * @since 2.4.0
 */
public abstract class AbstractSpringBean<T> {
    private final BeanUtils.Key key;
    private final String name;

    public AbstractSpringBean(@Nonnull Class<T> source, @Nonnull List<AutowireCandidateQualifier> qualifiers) {
        requireNonNull(source, "Argument 'source' must not be null");
        requireNonNull(qualifiers, "Argument 'qualifiers' must not be null");
        this.key = BeanUtils.Key.of(source, qualifiers);
        this.name = this.key.asFormattedName();
    }

    @Nonnull
    public Class<?> getType() {
        return key.getType();
    }

    @Nonnull
    public List<AutowireCandidateQualifier> getQualifiers() {
        return Collections.unmodifiableList(key.getQualifiers());
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public abstract void configure(@Nonnull String name, @Nonnull SpringContext context, @Nonnull DefaultListableBeanFactory factory);
}
