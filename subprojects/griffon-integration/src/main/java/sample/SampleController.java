package sample;

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonController;
import org.codehaus.griffon.core.compile.ArtifactProviderFor;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.awt.event.ActionEvent;

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
        System.out.println(sampleService);
        System.out.println("click " + event);
        System.out.println(model.getColor());
        System.out.println(model.getColor2());
        System.out.println(getApplication().getMessageSource().getMessage("sample.key"));
    }
}
