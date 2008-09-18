/**
 * Created by IntelliJ IDEA.
 *@author Danno.Ferrin
 * Date: Aug 5, 2008
 * Time: 10:35:06 PM
 */


Ant.property(environment: "env")
griffonHome = Ant.antProject.properties."env.GRIFFON_HOME"

includeTargets << new File("${griffonHome}/scripts/Package.groovy")
//includeTargets << new File ( "${griffonHome}/scripts/_PackagePlugins.groovy" )

target('default': "Runs the applicaiton from the command line") {
    depends(checkVersion)
    runApp()
}


target(runApp: "Does the actual command line execution") {
    depends(packageApp)

    // calculate the needed jars
    File jardir = new File(Ant.antProject.replaceProperties(config.griffon.jars.destDir))
    runtimeJars = []
    jardir.eachFileMatch(~/.*\.jar/) {f ->
        runtimeJars += f
    }

    // setup the vm
    if (!binding.variables.webstartVM) {
        webstartVM = [System.properties['java.home'], 'bin', 'javaws'].join(File.separator)
    }

    // start the processess
    Process p = "$webstartVM ${config.griffon.webstart.jnlp}".execute(null as String[], jardir)

    // pipe the output
    p.consumeProcessOutput(System.out, System.err)

    // wait for it.... wait for it...
    p.waitFor()
}