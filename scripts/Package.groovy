/*
* Copyright 2004-2005 the original author or authors.
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
 * Gant script that packages a Griffon application (note: does not create WAR)
 *
 * @deprecated This isn't much use as a Griffon command on its own.
 * @author Graeme Rocher
 *
 * @since 0.4
 */
includeTargets << griffonScript("_GriffonPackage")

target (_package: "Packages a Griffon application. Note: To create WAR use 'griffon war'") {
     depends(checkVersion, packageApp)
}

setDefaultTarget(_package)