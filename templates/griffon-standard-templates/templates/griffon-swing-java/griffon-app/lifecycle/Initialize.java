import griffon.core.GriffonApplication;
import org.codehaus.griffon.runtime.core.AbstractLifecycleHandler;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import static griffon.util.GriffonApplicationUtils.isMacOSX;

public class Initialize extends AbstractLifecycleHandler {
    @Inject
    public Initialize(@Nonnull GriffonApplication application) {
        super(application);
    }

    @Override
    public void execute() {
        if (isMacOSX()) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        } else {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
    }
}