package sample

import griffon.core.artifact.GriffonModel
import groovy.beans.Bindable
import org.codehaus.griffon.core.compile.ArtifactProviderFor

@ArtifactProviderFor(GriffonModel)
class SampleModel {
    @Bindable String input                                               //<1>
    @Bindable String output                                              //<1>
}
