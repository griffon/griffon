package sample.swing.java;

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonModel;
import griffon.metadata.ArtifactProviderFor;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel;

import javax.annotation.Nonnull;
import javax.inject.Inject;

@ArtifactProviderFor(GriffonModel.class)
public class SampleModel extends AbstractGriffonModel {
    private String input;                                                //<1>
    private String output;                                               //<1>

    @Inject
    public SampleModel(@Nonnull GriffonApplication application) {
        super(application);
    }

    public String getInput() {                                           //<2>
        return input;
    }

    public void setInput(String input) {
        firePropertyChange("input", this.input, this.input = input);     //<3>
    }
    
    public String getOutput() {                                          //<2>
        return output;
    }

    public void setOutput(String output) {
        firePropertyChange("output", this.output, this.output = output); //<3>
    }
}
