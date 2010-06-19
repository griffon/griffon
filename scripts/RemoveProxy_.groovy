
/**
 * @author Graeme Rocher (Grails 1.2.3)
 */

includeTargets << griffonScript("_GriffonArgParsing")
target(default:"Removes a proxy configuration") {
    depends(parseArguments)


    if(!argsMap.params) {
        println msg()
        exit 1
    }
    else {
        def settingsFile = griffonSettings.proxySettingsFile
        def config = griffonSettings.proxySettings
        def name = argsMap.params[0]
        config.remove(name)

        settingsFile.withWriter { w ->
            config.writeTo(w)
        }

        println "Removed proxy configuration [${name}]."
    }
}

String msg() {
    return '''\
Usage: griffon remove-proxy [name]
Example: griffon remove-proxy client
'''
}
