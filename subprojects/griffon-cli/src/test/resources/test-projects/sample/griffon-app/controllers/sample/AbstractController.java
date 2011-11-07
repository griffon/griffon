package sample;

import org.codehaus.griffon.runtime.core.AbstractGriffonController;

import java.awt.event.ActionEvent;

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
