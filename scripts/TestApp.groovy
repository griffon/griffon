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
 * Gant script that runs the Griffon unit tests
 *
 * @author Graeme Rocher
 *
 * @since 0.4
 */

includeTargets << griffonScript("_GriffonClean")
includeTargets << griffonScript("_GriffonTest")

target(default: "Run a Griffon applications unit tests") {
    depends(checkVersion, configureProxy, parseArguments, cleanTestReports)
 
    if (argsMap["unit"]) phasesToRun << "unit"
    if (argsMap["integration"]) phasesToRun << "integration"
//     if (argsMap["functional"]) phasesToRun << "functional"
    if (argsMap["other"]) phasesToRun << "other"
    if (argsMap["params"]) testNames = argsMap["params"]
    if (argsMap["xml"]) {
        reportFormats = [ "xml" ]
        createTestReports = false
    }
    else {
        createTestReports = !argsMap["no-reports"]
    }
 
    reRunTests = argsMap["rerun"]
 
    allTests()
}