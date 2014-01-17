package sample

import griffon.core.artifact.GriffonModel
import groovy.beans.Bindable
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonModel)
class SampleModel {
    @Bindable String input                                               //<1>
    @Bindable String output                                              //<1>
}
