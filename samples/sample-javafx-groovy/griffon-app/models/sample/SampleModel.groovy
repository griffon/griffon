package sample

import griffon.core.artifact.GriffonModel
import groovyx.javafx.beans.FXBindable
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonModel)
class SampleModel {
    @FXBindable String input                                             //<1>
    @FXBindable String output                                            //<1>
}
