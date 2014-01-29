package com.acme

import griffon.core.artifact.GriffonModel
import griffon.metadata.ArtifactProviderFor
import griffon.plugins.preferences.Preference

@ArtifactProviderFor(GriffonModel)
class NotPreferencesAwareModel implements ValueHolder {
    @Preference String value
}
