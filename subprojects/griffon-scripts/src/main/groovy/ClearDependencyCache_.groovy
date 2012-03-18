/*
 * Copyright 2012 the original author or authors.
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

/**
 * @author Andres Almiray
 */

target(name: 'clearDependencyCache',
        description: 'Removes dependencies from the Ivy cache',
        prehook: null, posthook: null) {
    File cacheDir = griffonSettings.dependencyManager.ivySettings.defaultCache

    String targetDirPath = cacheDir.absolutePath + File.separator

    if (argsMap.group) {
        targetDirPath += argsMap.group
        if (argsMap.name) {
            targetDirPath += File.separator + argsMap.name
        }
    } else if (argsMap.plugins) {
        targetDirPath += 'org.codehaus.griffon.plugins'
    } else if (argsMap.griffon) {
        targetDirPath += 'org.codehaus.griffon'
    } else if (!argsMap.all) {
        event 'StatusError', ["Missing arguments. Call 'griffon help clear-dependency-cache' to see the usage of this command"]
    }

    File targetDir = new File(targetDirPath)
    if (targetDir.exists()) {
        ant.delete(dir: targetDir)
    } else {
        println "Path ${targetDir} does not exist."
    }
}

setDefaultTarget('clearDependencyCache')