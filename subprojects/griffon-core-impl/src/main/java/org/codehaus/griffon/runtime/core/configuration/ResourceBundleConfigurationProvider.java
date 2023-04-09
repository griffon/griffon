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
package org.codehaus.griffon.runtime.core.configuration;

import griffon.converter.ConverterRegistry;
import griffon.core.Configuration;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.ResourceBundle;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.4.0
 */
public class ResourceBundleConfigurationProvider implements Provider<Configuration> {
    @Inject @Named("applicationResourceBundle")
    private ResourceBundle resourceBundle;

    @Inject
    private ConfigurationDecoratorFactory configurationDecoratorFactory;

    @Inject
    private ConverterRegistry converterRegistry;

    @Override
    public Configuration get() {
        requireNonNull(resourceBundle, "Argument 'resourceBundle' must not be null");
        requireNonNull(configurationDecoratorFactory, "Argument 'configurationDecoratorFactory' must not be null");
        ResourceBundleConfiguration configuration = new ResourceBundleConfiguration(converterRegistry, resourceBundle);
        return configurationDecoratorFactory.create(configuration);
    }
}
