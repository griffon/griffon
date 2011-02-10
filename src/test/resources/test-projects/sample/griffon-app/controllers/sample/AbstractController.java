package sample;

import java.awt.event.ActionEvent;
import org.codehaus.griffon.runtime.core.AbstractGriffonController;

public abstract class AbstractController extends AbstractGriffonController {
    private AbstractModel model;

    public void setModel(AbstractModel model) {
        this.model = model;
    }

    public AbstractModel getModel() {
        return model;
    }

    public void action(ActionEvent e) {
    }
}
