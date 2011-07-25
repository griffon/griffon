@artifact.package@import griffon.swing.BindUtils;
import griffon.util.CallableWithArgs;
import griffon.util.RunnableWithArgs;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;
import griffon.plugins.actions.ActionManager;
import static griffon.swing.SwingAction.action;

public class LicenseView extends AbstractDialogView {
    protected JComponent buildContent() {
        Action hideAction = action(ActionManager.getInstance().actionFor(controller, "hideAction"))
                .withName(message("application.action.Close.name", "Close"))
                .withMnemonic(message("application.action.Close.mnemonic", "C"))
                .withShortDescription(message("application.action.Close.short_description", "Close"))
                .build();

        MigLayout layout = new MigLayout();
        layout.setLayoutConstraints("fill");
        JPanel panel = new JPanel(layout);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        BindUtils.binding()
                .withSource(model)
                .withSourceProperty("license")
                .withTarget(textArea)
                .withTargetProperty("text")
                .make(getBuilder());
        BindUtils.binding()
                .withSource(model)
                .withSourceProperty("license")
                .withTarget(textArea)
                .withTargetProperty("caretPosition")
                .withConverter(new CallableWithArgs<Integer>() {
                    public Integer call(Object[] args) {
                        return 0;
                    }
                })
                .make(getBuilder());

        JScrollPane scrollPane = new JScrollPane(textArea);

        panel.add(scrollPane, "grow, wrap");
        panel.add(new JButton(hideAction), "right");

        String actionKey = "CloseAction";
        panel.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ESCAPE"), actionKey);
        panel.getActionMap().put(actionKey, hideAction);

        return panel;
    }
}
