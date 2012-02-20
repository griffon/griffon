import org.codehaus.griffon.artifacts.model.Plugin

/**
 * @author Andres Almiray
 */

includeTargets << griffonScript('PackagePlugin')

target(name: 'releasePlugin', description: 'Publishes a Griffon plugin release',
        prehook: null, posthook: null) {
    packageForRelease = true
    packagePlugin()
    createArtifactRelease(Plugin.TYPE, artifactInfo)

    if (argsMap['package-only']) exit(0)

    selectArtifactRepository()
    setupCredentials()
    event 'StatusUpdate', ["Contacting repository ${artifactRepository}"]
    try {
        if (artifactRepository.uploadRelease(release, username, password)) {
            event 'StatusFinal', ["Successfully published ${artifactInfo.name}-${artifactInfo.version} to ${artifactRepository.name}"]
        } else {
            event 'StatusError', ["Could not publish ${artifactInfo.name}-${artifactInfo.version} to ${artifactRepository.name}"]
        }
    } catch (x) {
        event 'StatusError', ["Could not publish ${artifactInfo.name}-${artifactInfo.version} to ${artifactRepository.name} => ${x}"]
    }
}

setDefaultTarget(releasePlugin)