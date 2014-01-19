package ${project_package}

import griffon.core.artifact.GriffonController
import griffon.metadata.ArtifactProviderFor

import javax.inject.Inject

@ArtifactProviderFor(GriffonController)
class ${project_capitalized_name}Controller {
    ${project_capitalized_name}Model model

    @Inject
    private ${project_capitalized_name}Service ${project_property_name}Service

    void sayHello() {
        String result = ${project_property_name}Service.sayHello(model.input)
        model.output = result
    }
}