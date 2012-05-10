/*
 * Copyright 2009-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.griffon.runtime.core;

import griffon.core.ArtifactInfo;
import griffon.core.GriffonApplication;
import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;

import static org.codehaus.griffon.runtime.util.GriffonApplicationHelper.loadClass;

/**
 * Default implementation of {@code ArtifactManager}.
 *
 * @author Andres Almiray
 * @since 0.9.2
 */
public class DefaultArtifactManager extends AbstractArtifactManager {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultArtifactManager.class);

    public DefaultArtifactManager(GriffonApplication app) {
        super(app);
    }

    protected Map<String, List<ArtifactInfo>> doLoadArtifactMetadata() {
        Map<String, List<ArtifactInfo>> artifacts = new LinkedHashMap<String, List<ArtifactInfo>>();
        try {
            Enumeration<URL> urls = getArtifactResourceLocations();
            ConfigSlurper slurper = new ConfigSlurper();
            while (urls.hasMoreElements()) {
                processURL(urls.nextElement(), slurper, artifacts);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

        return artifacts;
    }

    protected Enumeration<URL> getArtifactResourceLocations() throws IOException {
        return getApp().getClass().getClassLoader().getResources("META-INF/griffon-artifacts.properties");
    }

    private void processURL(URL url, ConfigSlurper slurper, Map<String, List<ArtifactInfo>> artifacts) {
        Properties p = new Properties();
        try {
            p.load(url.openStream());
        } catch (IOException e) {
            return;
        }

        ConfigObject config = slurper.parse(p);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Loading artifact definitions from " + url);
        }

        for (Object key : config.keySet()) {
            String type = key.toString();
            String classes = (String) config.get(type);
            if (classes.startsWith("'") && classes.endsWith("'")) {
                classes = classes.substring(1, classes.length() - 1);
            }
            String[] classNames = classes.split(",");
            if (LOG.isDebugEnabled()) {
                LOG.debug("Artifacts of type '" + type + "' = " + classNames.length);
            }

            List<ArtifactInfo> list = artifacts.get(type);
            if (list == null) {
                list = new ArrayList<ArtifactInfo>();
                artifacts.put(type, list);
            }

            for (String className : classNames) {
                try {
                    Class clazz = loadClass(className);
                    if (Modifier.isAbstract(clazz.getModifiers())) continue;
                    ArtifactInfo info = new ArtifactInfo(clazz, type);
                    if (!list.contains(info)) list.add(info);
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }
    }
}
