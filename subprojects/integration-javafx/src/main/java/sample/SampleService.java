package sample;

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonService;
import org.codehaus.griffon.core.compile.ArtifactProviderFor;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonService;

import javax.annotation.Nonnull;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

@ArtifactProviderFor(GriffonService.class)
public class SampleService extends AbstractGriffonService {
    @Inject
    public SampleService(@Nonnull GriffonApplication application) {
        super(application);
    }

    @PreDestroy
    public void destroy() {
        System.out.println("Goodbye cruel world!");
    }
}
