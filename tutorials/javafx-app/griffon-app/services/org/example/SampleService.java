package org.example;

import griffon.core.artifact.GriffonService;
import griffon.core.i18n.MessageSource;
import griffon.metadata.ArtifactProviderFor;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonService;

import static griffon.util.GriffonNameUtils.isBlank;
import static java.util.Arrays.asList;

@ArtifactProviderFor(GriffonService.class)
public class SampleService extends AbstractGriffonService {
    public String sayHello(String input) {
        MessageSource messageSource = getApplication().getMessageSource();
        if (isBlank(input)) {
            return messageSource.getMessage("greeting.default");
        } else {
            return messageSource.getMessage("greeting.parameterized", asList(input));
        }
    }
}