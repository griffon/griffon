package \${groupId}

import griffon.core.artifact.GriffonModel
import griffon.transform.beans.Observable
import org.kordamp.jipsy.ServiceProviderFor
import org.codehaus.griffon.runtime.pivot.artifact.AbstractPivotGriffonModel

@ServiceProviderFor(GriffonModel)
class AppModel extends AbstractPivotGriffonModel {
    @Observable int clickCount = 0
}