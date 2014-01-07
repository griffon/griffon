package sample;

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonController;
import org.codehaus.griffon.core.compile.ArtifactProviderFor;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController;

import javax.annotation.Nonnull;
import javax.inject.Inject;

@ArtifactProviderFor(GriffonController.class)
public class SampleController extends AbstractGriffonController {
    private SampleModel model;                                            // <1>

    @Inject
    private SampleService sampleService;                                  // <2>

    @Inject
    public SampleController(@Nonnull GriffonApplication application) {
        super(application);
    }

    public void setModel(SampleModel model) {
        this.model = model;
    }

    public void sayHello() {                                              // <3>
        final String result = sampleService.sayHello(model.getInput());
        System.out.println(result);
    }
}
