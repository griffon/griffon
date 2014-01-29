package com.acme

import griffon.core.artifact.GriffonModel
import griffon.metadata.ArtifactProviderFor
import griffon.plugins.preferences.Preference
import griffon.plugins.preferences.PreferencesAware

@PreferencesAware
@ArtifactProviderFor(GriffonModel)
class PreferencesAwareModel implements ValueHolder {
    @Preference String value
}
