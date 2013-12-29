/*
 * Copyright 2004-2014 the original author or authors.
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

import org.codehaus.griffon.artifacts.model.Plugin

/**
 * @author Andres Almiray
 */

includeTargets << griffonScript('_GriffonArtifacts')
includeTargets << griffonScript('_GriffonClean')

target(name: 'installPlugin', description: "Installs a plugin for the given URL or name and version", prehook: null, posthook: null) {
    resolveFrameworkFlag()
    ant.mkdir(dir: artifactSettings.artifactBase(Plugin.TYPE, framework))
    installArtifact(Plugin.TYPE)
    resetDependencyResolution()
    if (isApplicationProject && !argsMap.noclean) cleanAll()
}

setDefaultTarget(installPlugin)
