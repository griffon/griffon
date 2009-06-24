/**
 * Created by IntelliJ IDEA.
 *@author Danno.Ferrin
 * Date: Aug 5, 2008
 * Time: 10:35:06 PM
 */

import org.codehaus.griffon.commons.ConfigurationHolder
import org.codehaus.griffon.util.BuildSettings


includeTargets << griffonScript("Package")
includeTargets << griffonScript("_PackagePlugins" )

target(tweakConfig:' tweaks for webstart') {
    configTweaks << { config.griffon.jars.sign = true }
}
target(runWebstart: "Does the actual command line execution") {
    depends(checkVersion, tweakConfig, createConfig, packageApp)

    if ((config.griffon.jars.sign != [:]) && !config.griffon.jars.sign) {
        event("StatusFinal", ["Cannot run WebStart application because Webstart requires code signing.\n  in Config.groovy griffon.jars.sign = false"])
        exit(1)
    }

    // calculate the needed jars
    File jardir = new File(ant.antProject.replaceProperties(config.griffon.jars.destDir))
    // launch event after jardir has been defined
    event("RunWebstartStart",[])
    runtimeJars = []
    jardir.eachFileMatch(~/.*\.jar/) {f ->
        runtimeJars += f
    }

    // setup the vm
    if (!binding.variables.webstartVM) {
        webstartVM = [System.properties['java.home'], 'bin', 'javaws'].join(File.separator)
    }
    def javaOpts = []
    def env = System.getProperty(BuildSettings.ENVIRONMENT)
    javaOpts << "-D${BuildSettings.ENVIRONMENT}=${env}"
    javaOpts = "-J"+javaOpts.join("-J ")

    // TODO set proxy settings
    // start the processess
    Process p = "$webstartVM $javaOpts ${config.griffon.webstart.jnlp}".execute(null as String[], jardir)

    // pipe the output
    p.consumeProcessOutput(System.out, System.err)

    // wait for it.... wait for it...
    p.waitFor()
    event("RunWebstartEnd",[])
}

setDefaultTarget(runWebstart)
