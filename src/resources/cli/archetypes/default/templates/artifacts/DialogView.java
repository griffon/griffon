@artifact.package@import java.awt.*;
import javax.swing.*;
import java.util.Map;

import griffon.swing.SwingGriffonApplication;
import griffon.swing.WindowManager;
import griffon.util.RunnableWithArgs;
import static griffon.swing.SwingAction.action;
import org.codehaus.griffon.runtime.core.AbstractGriffonView;

public class @artifact.name@ extends AbstractGriffonView {
    private @artifact.name.plain@Controller controller;
    private @artifact.name.plain@Model model;
    private JDialog dialog;
    private JComponent content;

    public void setController(@artifact.name.plain@Controller controller) {
        this.controller = controller;
    }

    public void setModel(@artifact.name.plain@Model model) {
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

    private JComponent buildContent() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Content goes here"), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(1, 2));
        panel.add(buttons, BorderLayout.SOUTH);
        Action cancelAction = action("Cancel")
                .withMnemonic("C")
                .withShortDescription("Cancel")
                .withRunnable(new RunnableWithArgs() {
                    public void run(Object[] args) {
                        controller.hide();
                    }
                }).build();
        buttons.add(new JButton(cancelAction));
        buttons.add(new JButton(
            action("Ok")
                .withMnemonic("K")
                .withShortDescription("Ok")
                .withRunnable(new RunnableWithArgs() {
                    public void run(Object[] args) {
                        controller.hide();
                    }
                }).build()
        ));

        String actionKey = "CancelAction";
        panel.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ESCAPE"), actionKey);
        panel.getActionMap().put(actionKey, cancelAction);

        return panel;
    }

    public void show(final Window window) {
        execSync(new Runnable() {
            public void run() {
                Window owner = findOwner(window);
                if(dialog == null || dialog.getOwner() != owner) {
                    getWindowManager().hide(dialog);
                    dialog = buildDialog(owner);
                }
                relocateDialog(owner);
                getWindowManager().show(dialog);
            }
        });
    }

    public void hide() {
        execSync(new Runnable() {
            public void run() {
                if(dialog != null) {
                    getWindowManager().hide(dialog);
                    dialog = null;
                }
            }
        });
    }

    private Window findOwner(Window window) {
        if(window != null) return window;
        for(Window w : Window.getWindows()) {
            if(w.isFocused()) return w;
        }
        return null;
    }

    private JDialog buildDialog(Window window) {
        dialog = new JDialog(window, model.getTitle());
        dialog.setResizable(model.isResizable());
        dialog.setModal(model.isModal());
        dialog.getContentPane().add(content);
        if(model.getWidth() > 0 && model.getHeight() > 0) {
            dialog.setPreferredSize(new Dimension(model.getWidth(), model.getHeight()));
        }
        dialog.pack();
        return dialog;
    }

    private void relocateDialog(Window window) {
        int x = window.getX() + (window.getWidth() - dialog.getWidth()) / 2;
        int y = window.getY() + (window.getHeight() - dialog.getHeight()) / 2;
        dialog.setLocation(x, y);
    } 

    private WindowManager getWindowManager() {
        return ((SwingGriffonApplication) getApp()).getWindowManager();
    }
}
