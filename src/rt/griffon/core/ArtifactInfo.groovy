/*
 * Copyright 2009 the original author or authors.
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

package griffon.core

import griffon.util.IGriffonApplication
import java.beans.Introspector

/**
 * Describes and artifact and its basic information.
 *
 * @author Andres Almiray (aalmiray)
 */
// @Immutable
final class ArtifactInfo {
    final IGriffonApplication app
    final Class klass
    final String type
    final String simpleName
    final List<String> declaredProperties

    ArtifactInfo(IGriffonApplication app, Class klass, String type) {
        this.app = app
        this.klass = klass
        this.type = type

        def sn = klass.simpleName
        if(sn.length() == 1) {
            simpleName = sn
        } else {
            simpleName = sn[0].toLowerCase() + sn[1..-1]
        }

        declaredProperties = Introspector.getBeanInfo(klass).propertyDescriptors.name - ["class", "metaClass"]
    }

    String getName() {
        klass.name
    }

    String toString() {
        "${type}[$simpleName => ${klass.name}]"
    }

    /**
     * Creates a new instance of this artifact's klass.<p>
     * Will trigger a "NewInstance" application event.
     */
    def newInstance() {
        app.newInstance(klass, type)
    }

    /**
     * Returns a list of all dynamic properties found in the artifact's class
     */
    public List<String> getDynamicProperties() {
        klass.metaClass.properties.name - declaredProperties - ["class", "metaClass"]
    }

    /**
     * Returns a list of all properties found in the artifact's class excluding
     * 'class' and 'metaClass'.
     */
    public List<String> getAllProperties() {
        klass.metaClass.properties.name - ["class", "metaClass"]
    }
}
