package sample

import griffon.core.artifact.GriffonModel
import groovyx.javafx.beans.FXBindable
import org.codehaus.griffon.core.compile.ArtifactProviderFor

@ArtifactProviderFor(GriffonModel)
class SampleModel {
    @FXBindable String input                                             //<1>
}
