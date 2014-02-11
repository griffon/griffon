package sample;

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonModel;
import griffon.metadata.ArtifactProviderFor;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel;

import javax.annotation.Nonnull;
import javax.inject.Inject;

@ArtifactProviderFor(GriffonModel.class)
public class SampleModel extends AbstractGriffonModel {
    private StringProperty input;                                          //<1>
    private StringProperty output;                                         //<1>

    @Inject
    public SampleModel(@Nonnull GriffonApplication application) {
        super(application);
    }

    @Nonnull
    public final StringProperty inputProperty() {                          //<2>
        if (input == null) {
            input = new SimpleStringProperty(this, "input");
        }
        return input;
    }

    public void setInput(String input) {                                   //<3>
        inputProperty().set(input);
    }

    public String getInput() {                                             //<3>
        return input == null ? null : inputProperty().get();
    }

    @Nonnull
    public final StringProperty outputProperty() {                         //<2>
        if (output == null) {
            output = new SimpleStringProperty(this, "output");
        }
        return output;
    }

    public void setOutput(String output) {                                 //<3>
        outputProperty().set(output);
    }

    public String getOutput() {                                            //<3>
        return output == null ? null : outputProperty().get();
    }
}
