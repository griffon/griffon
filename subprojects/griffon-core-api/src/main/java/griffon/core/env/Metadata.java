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
package griffon.core.env;

import griffon.annotations.core.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import static griffon.util.StringUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * Represents the application Metadata
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public class Metadata {
    public static final String FILENAME = "application.properties";
    public static final String APPLICATION_VERSION = "application.version";
    public static final String APPLICATION_NAME = "application.name";
    private static final Logger LOG = LoggerFactory.getLogger(Metadata.class);

    private final Properties properties = new Properties();

    public Metadata(@Nonnull String path) {
        requireNonBlank(path, "Argument 'path' must not be blank");
        loadProperties(path);
    }

    public Metadata(@Nonnull File file) {
        requireNonNull(file, "Argument 'file' must not be null");
        loadProperties(file);
    }

    public Metadata(@Nonnull InputStream in) {
        requireNonNull(in, "Argument 'in' must not be null");
        try {
            loadProperties(in);
        } catch (IOException e) {
            LOG.warn("Could not load application metadata from inpustream.", e);
        }
    }

    private void loadProperties(String path) {
        URL url = Metadata.class.getClassLoader().getResource(path);
        if (url != null) {
            try {
                loadProperties(url.openStream());
            } catch (IOException e) {
                LOG.warn("Could not load application metadata from " + path, e);
            }
        }
    }

    private void loadProperties(File file) {
        try {
            loadProperties(new FileInputStream(file));
        } catch (IOException e) {
            LOG.warn("Could not load application metadata from " + file.getAbsolutePath(), e);
        }
    }

    private void loadProperties(InputStream in) throws IOException {
        properties.load(in);
    }

    /**
     * @return The application version
     */
    public String getApplicationVersion() {
        return get(APPLICATION_VERSION);
    }

    /**
     * @return The environment the application expects to run in
     */
    public String getEnvironment() {
        return get(Environment.KEY);
    }

    /**
     * @return The application name
     */
    public String getApplicationName() {
        return get(APPLICATION_NAME);
    }

    public String get(@Nonnull String key) {
        return properties.getProperty(requireNonBlank(key, "Argument 'key' must not be blank"));
    }

    @Nonnull
    public Set<String> keySet() {
        Set<String> keys = new LinkedHashSet<>();
        for (Object key : properties.keySet()) {
            keys.add(String.valueOf(key));
        }
        return Collections.unmodifiableSet(keys);
    }
}
