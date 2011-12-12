@artifact.package@import javax.swing.*;

import net.miginfocom.swing.MigLayout;
import griffon.plugins.actions.ActionManager;
import static griffon.swing.SwingAction.action;

public class PreferencesView extends AbstractDialogView {
    protected JComponent buildContent() {
        Action hideAction = action(ActionManager.getInstance().actionFor(controller, "hideAction"))
                .withName(message("application.action.Close.name", "Close"))
                .withMnemonic(message("application.action.Close.mnemonic", "C"))
                .withShortDescription(message("application.action.Close.short_description", "Close"))
                .build();

        MigLayout layout = new MigLayout();
        layout.setLayoutConstraints("fill");
        JPanel panel = new JPanel(layout);

        JPanel content = new JPanel();
        panel.add(content, "grow, wrap");
        panel.add(new JButton(hideAction), "right");

        String actionKey = "CloseAction";
        panel.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ESCAPE"), actionKey);
        panel.getActionMap().put(actionKey, hideAction);

        return panel;
    }
}
