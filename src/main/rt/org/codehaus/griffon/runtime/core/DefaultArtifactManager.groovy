/*
 * Copyright 2009-2011 the original author or authors.
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

package org.codehaus.griffon.runtime.core

import griffon.core.ArtifactInfo
import griffon.core.GriffonApplication
import java.lang.reflect.Modifier
import org.codehaus.griffon.runtime.util.GriffonApplicationHelper
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Default implementation of {@code ArtifactManager}.
 *
 * @author Andres Almiray
 * @since 0.9.2
 */
class DefaultArtifactManager extends AbstractArtifactManager {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultArtifactManager)

    DefaultArtifactManager(GriffonApplication app) {
        super(app)
    }

    Map<String, List<ArtifactInfo>> doLoadArtifactMetadata() {
        Map<String, List<ArtifactInfo>> artifacts = [:]
        Enumeration<URL> urls = app.class.classLoader.getResources('META-INF/griffon-artifacts.properties')

        ConfigSlurper slurper = new ConfigSlurper()
        urls.each { URL url ->
            Properties p = new Properties()
            p.load(url.openStream())
            ConfigObject config = slurper.parse(p)
            if(LOG.debugEnabled) LOG.debug("Loading artifact definitions from $url")
            config.each { String type, String classes ->
                if(classes.startsWith("'") && classes.endsWith("'")) classes = classes[1..-2]
                if(LOG.debugEnabled) LOG.debug("Artifacts of type '${type}' = ${classes.split(',').size()}")
                List list = artifacts.get(type, [])
                for(className in classes.split(',')) {
                    Class clazz = GriffonApplicationHelper.loadClass(app, className)
                    if(Modifier.isAbstract(clazz.modifiers)) continue
                    ArtifactInfo info = new ArtifactInfo(clazz, type)
                    if(!list.contains(info)) list << info
                }
            }
        }

        return artifacts
    }
}
