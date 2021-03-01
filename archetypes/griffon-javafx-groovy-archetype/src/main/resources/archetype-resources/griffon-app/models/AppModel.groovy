package \${groupId}

import griffon.core.artifact.GriffonModel
import griffon.transform.javafx.FXObservable
import org.kordamp.jipsy.annotations.ServiceProviderFor
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel

@ServiceProviderFor(GriffonModel)
class AppModel extends AbstractGriffonModel {
    @FXObservable String clickCount = "0"
}