package sample.lanterna.java;

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonController;
import griffon.metadata.ArtifactProviderFor;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController;
import sample.lanterna.java.SampleModel;
import sample.lanterna.java.SampleService;

import javax.annotation.Nonnull;
import javax.inject.Inject;

@ArtifactProviderFor(GriffonController.class)
public class SampleController extends AbstractGriffonController {
    private SampleModel model;                                             //<1>

    @Inject
    private SampleService sampleService;                                   //<2>

    @Inject
    public SampleController(@Nonnull GriffonApplication application) {
        super(application);
    }

    public void setModel(SampleModel model) {
        this.model = model;
    }

    public void sayHello() {                                               //<3>
        final String result = sampleService.sayHello(model.getInput());
        runInsideUIAsync(new Runnable() {                                  //<4>
            @Override
            public void run() {
                model.setOutput(result);
            }
        });
    }
}
