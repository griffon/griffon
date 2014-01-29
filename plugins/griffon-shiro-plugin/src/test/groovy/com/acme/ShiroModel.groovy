package com.acme

import griffon.core.artifact.GriffonModel
import griffon.metadata.ArtifactProviderFor
import groovy.beans.Bindable

@ArtifactProviderFor(GriffonModel)
class ShiroModel {
    @Bindable String value
}
