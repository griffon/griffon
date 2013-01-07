/*
 * Copyright 2004-2013 the original author or authors.
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
 * @author Graeme Rocher (Grails 1.2.3)
 */

target(name: 'addProxy', description: "Adds a proxy configuration", prehook: null, posthook: null) {
    if (!argsMap.params) {
        println msg()
        exit 1
    }

    if (argsMap.host && argsMap.port) {
        def settingsFile = griffonSettings.proxySettingsFile
        def config = griffonSettings.proxySettings

        config[argsMap.params[0]] = ['http.proxyHost': argsMap.host,
                'http.proxyPort': argsMap.port,
                "http.proxyUserName": argsMap.username ?: '',
                "http.proxyPassword": argsMap.password ?: '']

        settingsFile.withWriter { w -> config.writeTo(w) }

        println "Added proxy ${argsMap.params[0]} to ${settingsFile}"
    }
    else {
        println msg()
        exit 1
    }
}
setDefaultTarget(addProxy)

String msg() {
    return '''\
Usage: griffon add-proxy [name] --host=[server] --port=[port] --username=[username]* --password=[password]*
Example: griffon add-proxy client --host=proxy-server --port=4300 --username=guest --password=guest

* Optional
'''
}
