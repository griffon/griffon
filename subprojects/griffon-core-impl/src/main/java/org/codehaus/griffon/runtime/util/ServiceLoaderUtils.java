/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2022 the original author or authors.
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static griffon.core.GriffonExceptionHandler.sanitize;
import static griffon.util.StringUtils.isBlank;
import static griffon.util.StringUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class ServiceLoaderUtils {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceLoaderUtils.class);
    private static final String HASH = "#";

    private ServiceLoaderUtils() {
        // prevent instantiation
    }

    public static boolean load(@Nonnull ClassLoader classLoader, @Nonnull String path, @Nonnull Class<?> type, @Nonnull LineProcessor processor) {
        requireNonNull(classLoader, "Argument 'classLoader' must not be null");
        requireNonBlank(path, "Argument 'path' must not be blank");
        requireNonNull(type, "Argument 'type' must not be null");
        requireNonNull(processor, "Argument 'processor' must not be null");
        // "The name of a resource is a /-separated path name that identifies the resource."
        String normalizedPath = path.endsWith("/") ? path : path + "/";

        Enumeration<URL> urls;

        try {
            urls = classLoader.getResources(normalizedPath + type.getName());
        } catch (IOException ioe) {
            LOG.error(ioe.getClass().getName() + " error loading resources of type \"" + type.getName() + "\" from \"" + normalizedPath + "\".");
            return false;
        }

        if (urls == null) { return false; }

        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            LOG.debug("Reading {} definitions from {}", type.getName(), url);

            try (Scanner scanner = new Scanner(url.openStream())) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.startsWith(HASH) || isBlank(line)) { continue; }
                    processor.process(classLoader, type, line);
                }
            } catch (IOException e) {
                LOG.warn("Could not load " + type.getName() + " definitions from " + url, sanitize(e));
            }
        }

        return true;
    }

    public static boolean load(@Nonnull ClassLoader classLoader, @Nonnull String path, @Nonnull PathFilter pathFilter, @Nonnull ResourceProcessor processor) {
        requireNonNull(classLoader, "Argument 'classLoader' must not be null");
        requireNonBlank(path, "Argument 'path' must not be blank");
        requireNonNull(pathFilter, "Argument 'pathFilter' must not be blank");
        requireNonNull(processor, "Argument 'processor' must not be null");

        Enumeration<URL> urls;

        try {
            urls = classLoader.getResources(path);
        } catch (IOException ioe) {
            LOG.debug(ioe.getClass().getName() + " error loading resources from \"" + path + "\".");
            return false;
        }

        if (urls == null) { return false; }

        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            LOG.debug("Reading definitions from " + url);
            switch (url.getProtocol()) {
                case "file":
                    handleFileResource(url, classLoader, path, pathFilter, processor);
                    break;
                case "jar":
                    handleJarResource(url, classLoader, path, pathFilter, processor);
                    break;
                default:
                    LOG.warn("Could not load definitions from " + url);
            }
        }

        return true;
    }

    private static void handleFileResource(@Nonnull URL url, @Nonnull ClassLoader classLoader, @Nonnull String path, @Nonnull PathFilter pathFilter, @Nonnull ResourceProcessor processor) {
        try {
            File file = new File(url.toURI());
            for (File entry : file.listFiles()) {
                if (pathFilter.accept(entry.getName())) {
                    try (Scanner scanner = new Scanner(entry)) {
                        while (scanner.hasNextLine()) {
                            String line = scanner.nextLine();
                            if (line.startsWith(HASH) || isBlank(line)) { continue; }
                            processor.process(classLoader, line);
                        }
                    } catch (IOException e) {
                        LOG.warn("An error occurred while loading resources from " + entry.getAbsolutePath(), sanitize(e));
                    }
                }
            }
        } catch (URISyntaxException e) {
            LOG.warn("An error occurred while loading resources from " + url, sanitize(e));
        }
    }

    private static void handleJarResource(@Nonnull URL url, @Nonnull ClassLoader classLoader, @Nonnull String path, @Nonnull PathFilter pathFilter, @Nonnull ResourceProcessor processor) {
        try {
            URLConnection urlConnection = url.openConnection();
            if (urlConnection instanceof JarURLConnection) {
                JarURLConnection jarURLConnection = (JarURLConnection) urlConnection;
                JarFile jar = jarURLConnection.getJarFile();
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    if (jarEntry.getName().startsWith(path) && pathFilter.accept(jarEntry.getName())) {
                        try (Scanner scanner = new Scanner(jar.getInputStream(jarEntry))) {
                            while (scanner.hasNextLine()) {
                                String line = scanner.nextLine();
                                if (line.startsWith(HASH) || isBlank(line)) { continue; }
                                processor.process(classLoader, line);
                            }
                        } catch (IOException e) {
                            LOG.warn("An error occurred while loading resources from " + jarEntry.getName(), sanitize(e));
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOG.warn("An error occurred while loading resources from " + url, sanitize(e));
        }
    }

    public interface PathFilter {
        boolean accept(@Nonnull String path);
    }

    public interface LineProcessor {
        void process(@Nonnull ClassLoader classLoader, @Nonnull Class<?> type, @Nonnull String line);
    }

    public interface ResourceProcessor {
        void process(@Nonnull ClassLoader classLoader, @Nonnull String line);
    }
}
