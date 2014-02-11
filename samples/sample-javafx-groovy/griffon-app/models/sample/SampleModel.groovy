package sample

import griffon.core.artifact.GriffonModel
import griffon.metadata.ArtifactProviderFor
import groovyx.javafx.beans.FXBindable

@ArtifactProviderFor(GriffonModel)
class SampleModel {
    @FXBindable String input                                             //<1>
    @FXBindable String output                                            //<1>
}
