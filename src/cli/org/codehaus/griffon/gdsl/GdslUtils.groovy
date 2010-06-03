/*
 * Copyright 2010 the original author or authors.
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

package org.codehaus.griffon.gdsl

/**
 * @author Andres Almiray
 */
class GdslUtils {
    static String getFilePath(target) {
        if(!target) return false
        target?.containingFile?.virtualFile?.path ?: ''
    }

    static String getParentPath(target) {
        if(!target) return false
        target?.containingFile?.containingDirectory?.virtualFile?.path ?: ''
    }

    static boolean isGriffonArtifact(target) {
        getParentPath(target) ==~ /.*\/griffon-app\/.*/
    }

    static boolean isGriffonArtifactNotResource(target) {
        if(!isGriffonArtifact(target)) return false
        def filePath = getParentPath(target) 
        return !(filePath ==~ /.*\/griffon-app\/conf.*/) && !(filePath ==~ /.*\/griffon-app\/resources.*/)
    }
}
