package ${project_package}

import griffon.core.artifact.GriffonModel
import griffon.transform.beans.ObserObservable
import org.kordamp.jipsy.annotations.ServiceProviderFor
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel

@ServiceProviderFor(GriffonModel)
class ${project_class_name}Model extends AbstractGriffonModel {
    @FXObservable String clickCount = "0"
}