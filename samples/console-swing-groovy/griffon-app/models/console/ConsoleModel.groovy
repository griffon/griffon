package console

import griffon.core.artifact.GriffonModel
import groovy.beans.Bindable
import org.codehaus.griffon.core.compile.ArtifactProviderFor

@ArtifactProviderFor(GriffonModel)
class ConsoleModel {
    String scriptSource

    @Bindable Object scriptResult

    @Bindable boolean enabled = true
}
