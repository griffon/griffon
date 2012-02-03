/*
 * Copyright 2011-2012 the original author or authors.
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

/**
 * @author Andres Almiray
 * @since 0.9.5
 */
final class ArtifactRepositoryRegistry {
    private static final Map<String, ArtifactRepository> REPOSITORIES = [:].asSynchronized()

    private static final ArtifactRepositoryRegistry INSTANCE

    static {
        INSTANCE = new ArtifactRepositoryRegistry()
    }

    static ArtifactRepositoryRegistry getInstance() {
        INSTANCE
    }

    private ArtifactRepositoryRegistry() {}

    void registerRepository(ArtifactRepository repository) {
        if (!repository.name) {
            throw new IllegalArgumentException('Repositories must have a name!')
        }
        REPOSITORIES[repository.name] = repository
    }

    ArtifactRepository findRepository(String name) {
        REPOSITORIES[name]
    }

    void withRepositories(Closure closure) {
        synchronized (REPOSITORIES) {
            REPOSITORIES.each { name, repository ->
                closure(name, repository)
            }
        }
    }

    void configureRepositories() {
        REPOSITORIES.clear()

        ConfigObject config = BuildSettingsHolder.settings.loadConfig()

        ArtifactRepository griffonLocal = new LocalArtifactRepository(name: ArtifactRepository.DEFAULT_LOCAL_NAME)
        griffonLocal.path = ArtifactRepository.DEFAULT_LOCAL_LOCATION
        registerRepository(griffonLocal)

        ArtifactRepository griffonCentral = new RemoteArtifactRepository(name: ArtifactRepository.DEFAULT_REMOTE_NAME)
        griffonCentral.url = ArtifactRepository.DEFAULT_REMOTE_LOCATION

        config.griffon?.artifact?.repositories?.each { String name, Map repoConfig ->
            ArtifactRepository repository = null
            if (name == ArtifactRepository.DEFAULT_REMOTE_NAME) {
                if (repoConfig.username) griffonCentral.username = repoConfig.username
                if (repoConfig.password) griffonCentral.password = repoConfig.password
                if (repoConfig.port) griffonCentral.port = repoConfig.port
                return
            }

            if (!repoConfig.type) {
                throw new IllegalArgumentException("Misconfigured repository ${name} -> no type specified!")
            }
            switch (repoConfig.type) {
                case 'remote':
                    repository = new RemoteArtifactRepository(name: name)
                    if (!repoConfig.url) {
                        throw new IllegalArgumentException("Misconfigured repository ${name} -> no url specified!")
                    }
                    if (repoConfig.port) {
                        repository.port = repoConfig.port
                    }
                    if (repoConfig.timeout) {
                        repository.timeout = repoConfig.timeout
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
            registerRepository(repository)
        }

        //registerRepository(griffonCentral)
        registerRepository(new LegacyArtifactRepository())
    }
}
