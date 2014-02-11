package sample.swing.groovy

import griffon.core.artifact.GriffonModel
import griffon.metadata.ArtifactProviderFor
import griffon.transform.Observable

@ArtifactProviderFor(GriffonModel)
class SampleModel {
    @Observable String input                                             //<1>
    @Observable String output                                            //<1>
}
