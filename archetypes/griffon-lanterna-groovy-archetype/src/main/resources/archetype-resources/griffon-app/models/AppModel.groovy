package \${groupId}

import griffon.core.artifact.GriffonModel
import griffon.transform.beans.Observable
import org.kordamp.jipsy.ServiceProviderFor
import org.codehaus.griffon.runtime.lanterna.artifact.AbstractLanternaGriffonModel

@ServiceProviderFor(GriffonModel)
class AppModel extends AbstractLanternaGriffonModel {
    @Observable int clickCount = 0
}