package sample.pivot.java;

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonService;
import griffon.core.i18n.MessageSource;
import griffon.metadata.ArtifactProviderFor;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonService;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static griffon.util.GriffonNameUtils.isBlank;
import static java.util.Arrays.asList;

@ArtifactProviderFor(GriffonService.class)
public class SampleService extends AbstractGriffonService {
    @Inject
    public SampleService(@Nonnull GriffonApplication application) {
        super(application);
    }

    public String sayHello(String input) {
        MessageSource messageSource = getApplication().getMessageSource();
        if (isBlank(input)) {
            return messageSource.getMessage("greeting.default");
        } else {
            return messageSource.getMessage("greeting.parameterized", asList(input));
        }
    }
}