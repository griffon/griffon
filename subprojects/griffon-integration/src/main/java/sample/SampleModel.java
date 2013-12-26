package sample;

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonModel;
import griffon.core.resources.InjectedResource;
import org.codehaus.griffon.core.compile.ArtifactProviderFor;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.awt.Color;

@ArtifactProviderFor(GriffonModel.class)
public class SampleModel extends AbstractGriffonModel {
    @InjectedResource(defaultValue = "#0000FF")
    private Color color;

    @InjectedResource
    private Color color2;

    private String input;

    @Inject
    public SampleModel(@Nonnull GriffonApplication application) {
        super(application);
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        firePropertyChange("input", this.input, this.input = input);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        firePropertyChange("color", this.color, this.color = color);
    }

    public Color getColor2() {
        return color2;
    }

    public void setColor2(Color color2) {
        firePropertyChange("color2", this.color2, this.color2 = color2);
    }
}
