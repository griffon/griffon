package org.codehaus.griffon.artifacts

import org.codehaus.griffon.artifacts.model.Artifact
import org.codehaus.griffon.artifacts.model.Release

class MockArtifactRepository extends AbstractArtifactRepository {
    final String type = 'mock'
    final Map<String, Map<String, Artifact>> artifacts = [:]

    MockArtifactRepository() {
        name = 'mock'
    }

    List<Artifact> listArtifacts(String type) {
        List<Artifact> list = []
        list.addAll(artifacts.get(type, [:]).values() ?: [])
        list
    }

    Artifact findArtifact(String type, String name) {
        artifacts.get(type, [:])[name]
    }

    Artifact findArtifact(String type, String name, String version) {
        Artifact artifact = artifacts.get(type, [:])[name]
        if (artifact) {
            Release release = artifact.releases.find {it.version == version}
            return release ? artifact : null
        }
        null
    }

    File downloadFile(String type, String name, String version, String username) {
        return null
    }

    boolean uploadRelease(Release release, String username, String password) {
        return false
    }
}
