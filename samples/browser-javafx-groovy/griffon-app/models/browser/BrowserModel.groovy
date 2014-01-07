package browser

import griffon.core.artifact.GriffonModel
import groovyx.javafx.beans.FXBindable
import org.codehaus.griffon.core.compile.ArtifactProviderFor

@ArtifactProviderFor(GriffonModel)
class BrowserModel {
    @FXBindable String status = ''
    @FXBindable String url
}
