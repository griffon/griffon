/*
 * Copyright 2011 the original author or authors.
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

package org.codehaus.griffon.artifacts

import griffon.util.BuildSettingsHolder
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
@Singleton
class ArtifactRepositoryRegistry {
    private static final ConcurrentHashMap<String, ArtifactRepository> REPOSITORIES = [:]

    void registerRepository(ArtifactRepository repository) {
        if (!repository.name) {
            throw new IllegalArgumentException('Repositories must have a name!')
        }
        REPOSITORIES[repository.name] = repository
    }

    ArtifactRepository findRepository(String name) {
        REPOSITORIES[name]
    }

    void configureRepositories() {
        ConfigObject config = BuildSettingsHolder.settings.loadConfig()

        ArtifactRepository repository = new RemoteArtifactRepository(name: ArtifactRepository.DEFAULT)
        repository.url = 'http://localhost:8080/griffon-artifact-portal/'
        registerRepository(repository)

        config.griffon?.artifact?.repositories?.each { String name, Map repoConfig ->
            repository = null
            if (name == ArtifactRepository.DEFAULT) {
                repository = REPOSITORIES[ArtifactRepository.DEFAULT]
                if (repoConfig.username) repository.username = repoConfig.username
                if (repoConfig.password) repository.password = repoConfig.password
            }
            if (!repository) {
                if (!repoConfig.type) {
                    throw new IllegalArgumentException("Misconfigured repository ${name} -> no type specified!")
                }
                switch (repoConfig.type) {
                    case 'remote':
                        repository = new RemoteArtifactRepository(name: name)
                        if (!repoConfig.url) {
                            throw new IllegalArgumentException("Misconfigured repository ${name} -> no url specified!")
                        }
                        repository.url = repoConfig.url
                        if (repoConfig.username) repository.username = repoConfig.username
                        if (repoConfig.password) repository.password = repoConfig.password
                        break
                    case 'local':
                        repository = new LocalArtifactRepository(name: name)
                        if (!repoConfig.path) {
                            throw new IllegalArgumentException("Misconfigured repository ${name} -> no path specified!")
                        }
                        repository.path = repoConfig.path
                        break
                }
            }
            registerRepository(repository)
        }
    }
}
