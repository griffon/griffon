package browser

import griffon.core.artifact.GriffonModel
import groovyx.javafx.beans.FXBindable
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonModel)
class BrowserModel {
    @FXBindable String status = ''
    @FXBindable String url
}
