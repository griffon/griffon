@artifact.package@import java.awt.*;
import javax.swing.*;
import java.util.Map;

import griffon.swing.SwingGriffonApplication;
import griffon.swing.WindowManager;
import griffon.plugins.i18n.MessageSourceHolder;
import org.codehaus.griffon.runtime.core.AbstractGriffonView;

public abstract class AbstractDialogView extends AbstractGriffonView {
    protected DialogController controller;
    protected AbstractDialogModel model;
    protected JDialog dialog;
    protected JComponent content;

    public void setController(DialogController controller) {
        this.controller = controller;
    }

    public void setModel(AbstractDialogModel model) {
        this.model = model;
    }

    @Override
    public void mvcGroupInit(Map<String, Object> args) {
        execSync(new Runnable() {
            public void run() {
                content = buildContent();
            }
        });
    }

    protected abstract JComponent buildContent();

    public final void show(final Window window) {
        execSync(new Runnable() {
            public void run() {
                Window owner = findOwner(window);
                if (dialog == null || dialog.getOwner() != owner) {
                    getWindowManager().hide(dialog);
                    dialog = buildDialog(owner);
                }
                relocateDialog(owner);
                getWindowManager().show(dialog);
            }
        });
    }

    public final void hide() {
        execSync(new Runnable() {
            public void run() {
                if (dialog != null) {
                    getWindowManager().hide(dialog);
                    dialog = null;
                }
            }
        });
    }

    protected String message(String key, String defaultValue) {
        return MessageSourceHolder.getMessageSource().getMessage(key, defaultValue);
    }

    protected Window findOwner(Window window) {
        if (window != null) return window;
        for (Window w : Window.getWindows()) {
            if (w.isFocused()) return w;
        }
        return null;
    }

    protected JDialog buildDialog(Window window) {
        dialog = new JDialog(window, model.getTitle());
        dialog.setResizable(model.isResizable());
        dialog.setModal(model.isModal());
        dialog.getContentPane().add(content);
        if (model.getWidth() > 0 && model.getHeight() > 0) {
            dialog.setPreferredSize(new Dimension(model.getWidth(), model.getHeight()));
        }
        dialog.pack();
        return dialog;
    }

    protected void relocateDialog(Window window) {
        int x = window.getX() + (window.getWidth() - dialog.getWidth()) / 2;
        int y = window.getY() + (window.getHeight() - dialog.getHeight()) / 2;
        dialog.setLocation(x, y);
    }

    protected WindowManager getWindowManager() {
        return ((SwingGriffonApplication) getApp()).getWindowManager();
    }

    protected Image getImage(String path) {
        return Toolkit.getDefaultToolkit().getImage(
                getClass().getResource(path));
    }
}
