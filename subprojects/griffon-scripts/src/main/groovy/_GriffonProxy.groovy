/*
 * Copyright 2004-2012 the original author or authors.
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
 * Contains a target for configuring an HTTP proxy.
 *
 * @author Peter Ledbrook (Grails 1.1)
 */

target(name: 'configureProxy', description: 'Configure an HTTP proxy',
     prehook: null, posthook: null) {
    proxySettings = ''
    File scriptFile = new File("${userHome}/.griffon/scripts/ProxyConfig.groovy")
    if (!scriptFile.exists()) {
        return
    }

    includeTargets << scriptFile.text

    if (!proxyConfig.proxyHost) {
        return
    }

    // Let's configure proxy...
    String proxyHost = proxyConfig.proxyHost
    String proxyPort = proxyConfig.proxyPort ? proxyConfig.proxyPort : '80'
    String proxyUser = proxyConfig.proxyUser ? proxyConfig.proxyUser : ''
    String proxyPassword = proxyConfig.proxyPassword ? proxyConfig.proxyPassword : ''
    println "Configured HTTP proxy: ${proxyHost}:${proxyPort}${proxyConfig.proxyUser ? '(' + proxyUser + ')' : ''}"
    // ... for ant. We can remove this line with ant 1.7.0 as it uses system properties.
    ant.setproxy(proxyhost: proxyHost,
            proxyport: proxyPort,
            proxyuser: proxyUser,
            proxypassword: proxyPassword)
    // ... for all other code
    System.properties.putAll([
            'http.proxyHost': proxyHost,
            'http.proxyPort': proxyPort,
            'http.proxyUserName': proxyUser,
            'http.proxyPassword': proxyPassword])

    proxySettings += "-Dproxy.host=$proxyHost "
    proxySettings += "-Dproxy.port=$proxyPort "
    proxySettings += "-Dproxy.user=$proxyUser "
    proxySettings += "-Dproxy.password=$proxyPassword "
}
