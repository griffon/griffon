package \${groupId}

import griffon.core.artifact.GriffonModel
import griffon.transform.beans.Observable
import org.kordamp.jipsy.ServiceProviderFor
import org.codehaus.griffon.runtime.swing.artifact.AbstractSwingGriffonModel

@ServiceProviderFor(GriffonModel)
class AppModel extends AbstractSwingGriffonModel {
    @Observable int clickCount = 0
}