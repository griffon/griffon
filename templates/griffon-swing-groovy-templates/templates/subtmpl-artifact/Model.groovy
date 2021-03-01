package ${project_package}

import griffon.core.artifact.GriffonModel
import griffon.transform.beans.Observable
import org.kordamp.jipsy.annotations.ServiceProviderFor
import org.codehaus.griffon.runtime.swing.artifact.AbstractSwingGriffonModel

@ServiceProviderFor(GriffonModel)
class ${project_class_name}Model extends AbstractSwingGriffonModel {
    @Observable int clickCount = 0
}