package ${project_package}

import griffon.core.artifact.GriffonService
import griffon.core.i18n.MessageSource
import griffon.metadata.ArtifactProviderFor

import static griffon.util.GriffonNameUtils.isBlank

@ArtifactProviderFor(GriffonService)
class ${project_capitalized_name}Service {
    String sayHello(String input) {
        MessageSource ms = application.messageSource
        isBlank(input) ? ms.getMessage('greeting.default') : ms.getMessage('greeting', [input])
    }
}