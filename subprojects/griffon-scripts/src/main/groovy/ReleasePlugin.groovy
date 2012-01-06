import org.codehaus.griffon.artifacts.model.Plugin

/**
 * @author Andres Almiray
 */

includeTargets << griffonScript('PackagePlugin')

target(releasePlugin: 'Publishes a Griffon plugin release') {
    packageForRelease = true
    packagePlugin()
    createArtifactRelease(Plugin.TYPE, artifactInfo)
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