package sample

import griffon.core.artifact.GriffonModel
import griffon.core.resources.InjectedResource
import org.codehaus.griffon.core.compile.ArtifactProviderFor

import java.awt.Color

@ArtifactProviderFor(GriffonModel)
class SampleModel {
    @InjectedResource(defaultValue = '#0000FF')
    Color color

    @InjectedResource
    Color color2

    String input
}
