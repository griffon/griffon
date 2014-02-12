/*
 * Copyright 2011-2014 the original author or authors.
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

/**
 * Creates an alias for a command/params combination.
 * Original from http://d.hatena.ne.jp/kiy0taka/20110428/1303997034
 *
 * @author Kiyotaka Oku (@kiy0taka)
 */

target(name: 'createCommandAlias', description: 'Create Griffon command alias', prehook: null, posthook: null) {
    def params = argsMap["params"]
    if (params.size() < 2) {
        println """\
            |Usage:
            |griffon create-alias [alias-name] [target] [arguments]*""".stripMargin()
    } else {
        def aliasScript = new File('scripts', GriffonUtil.getNameFromScript(params[0]) + '.groovy')
        aliasScript.text = """\
            |includeTargets << includeScript("${GriffonUtil.getNameFromScript(params[1])}")
            |
            |argsMap["params"] = ${params.size() > 2 ? params[2..-1].inspect() : '[]'}
            |""".stripMargin()
        println "Succesfully created alias '${params[0]}' for command '${params[1]}' as scripts/${aliasScript.name}"
    }
}

setDefaultTarget('createCommandAlias')
