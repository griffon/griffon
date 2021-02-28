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
package org.codehaus.griffon.runtime.util;

import griffon.annotations.core.Nonnull;
import griffon.core.resources.ResourceHandler;
import griffon.util.PropertiesReader;
import griffon.util.PropertiesResourceBundle;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.11.0
 */
@Named("properties")
public class PropertiesResourceBundleLoader extends AbstractResourceBundleLoader {
    protected static final String PROPERTIES_SUFFIX = ".properties";

    protected final PropertiesReader propertiesReader;

    @Inject
    public PropertiesResourceBundleLoader(@Nonnull ResourceHandler resourceHandler,
                                          @Nonnull PropertiesReader propertiesReader) {
        super(resourceHandler);
        this.propertiesReader = requireNonNull(propertiesReader, "Argument 'propertiesReader' must not be null");
    }

    @Nonnull
    @Override
    public Collection<ResourceBundle> load(@Nonnull String name) {
        requireNonBlank(name, ERROR_FILENAME_BLANK);
        List<ResourceBundle> bundles = new ArrayList<>();
        List<URL> resources = getResources(name, PROPERTIES_SUFFIX);
        if (resources != null) {
            for (URL resource : resources) {
                if (null == resource) { continue; }
                try {
                    Properties properties = propertiesReader.load(resource.openStream());
                    bundles.add(new PropertiesResourceBundle(properties));
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return bundles;
    }
}
