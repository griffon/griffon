/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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
package org.codehaus.griffon.runtime.core.i18n;

import griffon.annotations.core.Nonnull;
import griffon.core.bundles.CompositeResourceBundleBuilder;
import griffon.core.i18n.MessageSource;

import javax.inject.Inject;
import javax.inject.Provider;

import static griffon.util.StringUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class MessageSourceProvider implements Provider<MessageSource> {
    private final String basename;

    @Inject
    private CompositeResourceBundleBuilder resourceBundleBuilder;

    @Inject
    private MessageSourceDecoratorFactory messageSourceDecoratorFactory;

    public MessageSourceProvider(@Nonnull String basename) {
        this.basename = requireNonBlank(basename, "Argument 'basename' must not be blank");
    }

    @Override
    public MessageSource get() {
        requireNonNull(resourceBundleBuilder, "Argument 'resourceBundleBuilder' must not be null");
        requireNonNull(messageSourceDecoratorFactory, "Argument 'messageSourceDecoratorFactory' must not be null");
        DefaultMessageSource messageSource = new DefaultMessageSource(resourceBundleBuilder, basename);
        return messageSourceDecoratorFactory.create(messageSource);
    }
}
