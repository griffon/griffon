package ${project_package};

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonService;
import griffon.metadata.ArtifactProviderFor;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonService;

import javax.annotation.Nonnull;
import javax.inject.Inject;

@ArtifactProviderFor(GriffonService.class)
public class ${project_class_name}Service extends AbstractGriffonService {
    @Inject
    public ${project_class_name}Service(@Nonnull GriffonApplication application) {
        super(application);
    }
}