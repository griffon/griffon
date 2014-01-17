package console

import griffon.core.artifact.GriffonModel
import groovy.beans.Bindable
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonModel)
class ConsoleModel {
    String scriptSource                                  //<1>
    @Bindable Object scriptResult                        //<2>
    @Bindable boolean enabled = true                     //<3>
}
