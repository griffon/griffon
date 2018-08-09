package \${groupId}

import griffon.core.artifact.GriffonModel
import griffon.transform.javafx.FXObservable
import griffon.metadata.ArtifactProviderFor
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel

@ArtifactProviderFor(GriffonModel)
class AppModel extends AbstractGriffonModel {
    @FXObservable String clickCount = "0"
}