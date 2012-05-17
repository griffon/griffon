/*
* Copyright 2005-2012 the original author or authors.
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

import griffon.util.GriffonUtil

import static griffon.util.GriffonExceptionHandler.sanitize

includeTargets << griffonScript('_GriffonBootstrap')

/*
 * Adapted from http://naleid.com/blog/2010/12/03/grails-run-script-updated-for-grails-1-3-5/
 */

target(name: 'runScript', description: 'Execute the specified script(s) after starting up the application environment',
        prehook: null, posthook: null) {
    boolean doBootstrap = argsMap.bootstrap && (argsMap.bootstrap instanceof Boolean ?: Boolean.parseBoolean(argsMap.bootstrap))
    if (doBootstrap) {
        depends(bootstrap)
    } else {
        depends(setupApp)
    }

    if (!argsMap.params) {
        event('StatusError', ['ERROR: Required script name parameter is missing'])
        System.exit 1
    }

    def scriptFile = argsMap.params[0]
    String[] scriptArgs = new String[0]
    def scriptArgsStartIndex = 0
    unparsedArgs.eachWithIndex { arg, argIndex ->
        if (arg.equals(scriptFile)) {
            scriptArgsStartIndex = argIndex + 1
        }
    }
    if (scriptArgsStartIndex > 0 && scriptArgsStartIndex < unparsedArgs.length) {
        scriptArgs = unparsedArgs[scriptArgsStartIndex..-1] as String[]
    }

    event('StatusUpdate', ["Running script $scriptFile ..."])
    executeScript scriptFile, scriptArgs, classLoader, doBootstrap
    event('StatusUpdate', ["Script $scriptFile complete!"])
}

def executeScript(String scriptFile, String[] scriptArgs, ClassLoader classLoader, boolean doBootstrap) {
    File script = new File(scriptFile)
    if (!script.exists()) {
        event('StatusError', ["Designated script doesn't exist: $scriptFile"])
        return
    }

    Binding shellBinding = new Binding(
            applicationName: griffonAppName,
            applicationVersion: griffonAppVersion,
            griffonVersion: GriffonUtil.griffonVersion
    )
    if (doBootstrap) shellBinding.griffonApplication = griffonApp
    GroovyShell shell = new GroovyShell(classLoader, shellBinding)
    try {
        shell.run(script.text, scriptFile, scriptArgs)
    } catch (x) {
        sanitize(x).printStackTrace()
    }
}

setDefaultTarget runScript
