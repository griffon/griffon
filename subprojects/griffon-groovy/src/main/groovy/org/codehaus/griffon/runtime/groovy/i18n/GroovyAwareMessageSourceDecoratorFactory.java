/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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
package org.codehaus.griffon.runtime.groovy.i18n;

import griffon.annotations.core.Nonnull;
import griffon.core.i18n.MessageSource;
import org.codehaus.griffon.runtime.core.i18n.MessageSourceDecorator;
import org.codehaus.griffon.runtime.core.i18n.MessageSourceDecoratorFactory;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.4.0
 */
public class GroovyAwareMessageSourceDecoratorFactory implements MessageSourceDecoratorFactory {
    @Nonnull
    @Override
    public MessageSourceDecorator create(@Nonnull MessageSource messageSource) {
        requireNonNull(messageSource, "Argument 'messageSource' must not be null");
        return new DefaultGroovyAwareMessageSource(messageSource);
    }
}
