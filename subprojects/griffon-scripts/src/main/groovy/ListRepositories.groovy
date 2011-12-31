/*
* Copyright 2010-2011 the original author or authors.
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

import org.codehaus.griffon.artifacts.ArtifactRepository
import org.codehaus.griffon.artifacts.ArtifactRepositoryRegistry

/**
 * @author Andres Almiray
 */

includeTargets << griffonScript('_GriffonArtifacts')

target(listReposiories: 'Lists all configured repositories') {
    depends(parseArguments, configureProxy, configureArtifactRepositories)

    listRepositoriesHeader()
    ArtifactRepositoryRegistry.instance.withRepositories { String name, ArtifactRepository artifactRepository ->
        println formatRepositoryForPrint(artifactRepository)
    }
    listRepositoriesFooter()
}

setDefaultTarget(listReposiories)

listRepositoriesHeader = {->
    println """
Available repositories are listed below:
-----------------------------------------------------------------------
${'Name'.padRight(20, ' ')}${'Type'.padRight(8, ' ')} Location
"""
}

formatRepositoryForPrint = { ArtifactRepository repository ->
    "${repository.name.padRight(20, ' ')}${repository.type.toString().padRight(8, ' ')} ${repository.type == ArtifactRepository.LOCAL ? repository.path : repository.url}"
}

listRepositoriesFooter = {->
    println """
To find more info about available artifacts type

    'griffon list-plugins'
    'griffon list-archetypes'

For further info visit http://griffon.codehaus.org/
"""
}