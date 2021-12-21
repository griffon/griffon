package \${package}

import griffon.annotations.beans.Observable
import griffon.core.artifact.GriffonModel
import org.codehaus.griffon.runtime.swing.artifact.AbstractSwingGriffonModel
import org.kordamp.jipsy.annotations.ServiceProviderFor

@ServiceProviderFor(GriffonModel)
class _APPModel extends AbstractSwingGriffonModel {
    @Observable int clickCount = 0
}