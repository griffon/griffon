package sample;

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonController;
import javafx.event.ActionEvent;
import org.codehaus.griffon.core.compile.ArtifactProviderFor;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController;

import javax.annotation.Nonnull;
import javax.inject.Inject;

@ArtifactProviderFor(GriffonController.class)
public class SampleController extends AbstractGriffonController {
    private SampleModel model;

    @Inject
    private SampleService sampleService;

    @Inject
    public SampleController(@Nonnull GriffonApplication application) {
        super(application);
    }

    public void setModel(SampleModel model) {
        this.model = model;
    }

    public void click(ActionEvent event) {
        System.out.println("click " + model.getInput());
    }
}
