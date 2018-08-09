package ${project_package}

import griffon.core.artifact.GriffonModel
import griffon.transform.beans.Observable
import org.kordamp.jipsy.ServiceProviderFor
import org.codehaus.griffon.runtime.lanterna3.artifact.AbstractLanternaGriffonModel

@ServiceProviderFor(GriffonModel)
class ${project_class_name}Model extends AbstractLanternaGriffonModel{
    @Observable int clickCount = 0
}