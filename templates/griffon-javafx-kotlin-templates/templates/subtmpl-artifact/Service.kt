package ${project_package}

import griffon.core.artifact.GriffonService
import org.kordamp.jipsy.ServiceProviderFor
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonService

@javax.inject.Singleton
@ServiceProviderFor(GriffonService::class)
class ${project_class_name}Service : AbstractGriffonService() {

}
