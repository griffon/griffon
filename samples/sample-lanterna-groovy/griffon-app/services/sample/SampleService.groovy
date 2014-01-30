package sample

import griffon.core.artifact.GriffonService
import griffon.core.i18n.MessageSource
import griffon.metadata.ArtifactProviderFor

import static griffon.util.GriffonNameUtils.isBlank

@ArtifactProviderFor(GriffonService)
class SampleService {
    String sayHello(String input) {
        MessageSource ms = application.messageSource
        isBlank(input) ? ms.getMessage('greeting.default') : ms.getMessage('greeting.parameterized', [input])
    }
}
