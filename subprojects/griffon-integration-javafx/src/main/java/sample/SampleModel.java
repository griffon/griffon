package sample;

import griffon.core.GriffonApplication;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class SampleModel extends AbstractGriffonModel {
    private StringProperty input;

    @Inject
    public SampleModel(@Nonnull GriffonApplication application) {
        super(application);
    }

    public final StringProperty inputProperty() {
        if (input == null) {
            input = new SimpleStringProperty(this, "input");
        }
        return input;
    }

    public void setInput(String input) {
        inputProperty().set(input);
    }

    public String getInput() {
        return input == null ? null : inputProperty().get();
    }
}
