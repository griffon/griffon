/*
 * Copyright 2009-2010 the original author or authors.
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

/**
 * Handler for 'Service' artifacts.
 *
 * @author Andres Almiray
 */
class ServiceArtifactHandler extends ArtifactHandlerAdapter {
    public static final String TYPE = "service"
    public static final String TRAILING = "Service"

    private final Map SERVICES = [:]

    ServiceArtifactHandler() {
        super(TYPE)
    }

    void initialize(ArtifactInfo[] artifacts) {
        super.initialize(artifacts)
        if(app.config?.griffon?.basic_injection?.disable) return
        app.addApplicationEventListener(this)
    }

    /**
     * Application event listener.<p>
     * Lazily injects services instances if {@code app.config.griffon.basic_injection.disable}
     * is not set to true
     */
    def onNewInstance = { klass, t, instance ->
        if(type == t || app.config?.griffon?.basic_injection?.disable) return
        klass.metaClass.properties.name.each { propertyName ->
            if(SERVICES[propertyName]) {
                instance[propertyName] = SERVICES[propertyName]
            } else {
                def artifact = findArtifact(propertyName, false)
                if(artifact) {
                    def service = artifact.newInstance()
                    service.metaClass.app = app
                    SERVICES[propertyName] = service
                    instance[propertyName] = service
                }
            }
        }
    }
}
