package org.example;

import griffon.core.artifact.GriffonModel;
import griffon.metadata.ArtifactProviderFor;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel;

import javax.annotation.Nonnull;

@ArtifactProviderFor(GriffonModel.class)
public class SampleModel extends AbstractGriffonModel {
    private StringProperty input;
    private StringProperty output;

    @Nonnull
    public final StringProperty inputProperty() {
        if (input == null) {
            input = new SimpleStringProperty(this, "input");
        }
        return input;
    }

    public final StringProperty getInputProperty() {
        return inputProperty();
    }

    public String getInput() {
        return input == null ? null : inputProperty().get();
    }

    public void setInput(String input) {
        inputProperty().set(input);
    }

    @Nonnull
    public final StringProperty outputProperty() {
        if (output == null) {
            output = new SimpleStringProperty(this, "output");
        }
        return output;
    }

    public final StringProperty getOutputProperty() {
        return outputProperty();
    }

    public String getOutput() {
        return output == null ? null : outputProperty().get();
    }

    public void setOutput(String output) {
        outputProperty().set(output);
    }
}