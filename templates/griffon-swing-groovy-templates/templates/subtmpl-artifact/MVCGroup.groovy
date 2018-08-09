package ${project_package}

import javax.inject.Named
import griffon.core.mvc.MVCGroup
import org.codehaus.griffon.runtime.core.mvc.AbstractTypedMVCGroup
import griffon.annotations.core.Nonnull

@Named('${name}')
class ${project_class_name}MVCGroup extends AbstractTypedMVCGroup<${project_class_name}Model, ${project_class_name}View, ${project_class_name}Controller> {
    ${project_class_name}MVCGroup(@Nonnull MVCGroup delegate) {
        super(delegate)
    }
}