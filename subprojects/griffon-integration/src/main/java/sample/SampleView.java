package sample;


import griffon.core.GriffonApplication;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonView;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;

import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

public class SampleView extends AbstractGriffonView {
    private SampleController controller;
    private SampleModel model;

    @Inject
    public SampleView(@Nonnull GriffonApplication application) {
        super(application);
    }

    public void setController(SampleController controller) {
        this.controller = controller;
    }

    public void setModel(SampleModel model) {
        this.model = model;
    }

    @Override
    public void initUI() {
        JFrame window = (JFrame) getApplication().createApplicationContainer();
        window.setName("mainWindow");
        window.setSize(320, 240);
        window.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        getApplication().getWindowManager().attach("mainWindow", window);

        Action action = (Action) getApplication().getActionManager().actionFor(controller, "click").getToolkitAction();
        window.getContentPane().add(new JButton(action));
    }
}
