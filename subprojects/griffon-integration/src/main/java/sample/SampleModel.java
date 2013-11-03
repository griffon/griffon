package sample;

import griffon.core.GriffonApplication;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class SampleModel extends AbstractGriffonModel {
    private String input;

    @Inject
    public SampleModel(@Nonnull GriffonApplication application) {
        super(application);
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        firePropertyChange("inpit", this.input, this.input = input);
    }
}
