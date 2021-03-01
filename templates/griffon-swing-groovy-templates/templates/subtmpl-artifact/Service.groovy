package ${project_package}

import griffon.core.artifact.GriffonService
import org.kordamp.jipsy.annotations.ServiceProviderFor
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonService

@javax.inject.Singleton
@ServiceProviderFor(GriffonService)
class ${project_class_name}Service extends AbstractGriffonService {

}