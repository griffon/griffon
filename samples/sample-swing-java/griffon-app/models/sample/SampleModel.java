package sample;

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonModel;
import org.codehaus.griffon.core.compile.ArtifactProviderFor;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel;

import javax.annotation.Nonnull;
import javax.inject.Inject;

@ArtifactProviderFor(GriffonModel.class)
public class SampleModel extends AbstractGriffonModel {
    private String input;                                              //<1>

    @Inject
    public SampleModel(@Nonnull GriffonApplication application) {
        super(application);
    }

    public String getInput() {                                         //<2>
        return input;
    }

    public void setInput(String input) {
        firePropertyChange("input", this.input, this.input = input);   //<3>
    }
}
