package console

import griffon.core.artifact.GriffonModel
import griffon.transform.Observable
import griffon.metadata.ArtifactProviderFor

@ArtifactProviderFor(GriffonModel)
class ConsoleModel {
    String scriptSource                                  //<1>
    @Observable Object scriptResult                      //<2>
    @Observable boolean enabled = true                   //<3>
}
