package sample.swing.groovy

import griffon.core.artifact.GriffonModel
import griffon.transform.Observable
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonModel)
class SampleModel {
    @Observable String input                                             //<1>
    @Observable String output                                            //<1>
}
