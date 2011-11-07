package sample;

import org.codehaus.griffon.runtime.core.AbstractGriffonModel;

public abstract class AbstractModel extends AbstractGriffonModel {
    // an observable property
    private String input;

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        firePropertyChange("input", this.input, this.input = input);
    }
}
