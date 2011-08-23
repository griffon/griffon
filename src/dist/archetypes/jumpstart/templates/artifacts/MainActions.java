@artifact.package@import javax.swing.*;
import griffon.util.ApplicationHolder;
import griffon.util.RunnableWithArgs;
import griffon.plugins.i18n.MessageSourceHolder;
import griffon.plugins.actions.ActionManager;
import static griffon.swing.SwingAction.action;

public class @artifact.name.plain@Actions {
    public static Action newAction() {
        return actionFor("new");
    }

    public static Action openAction() {
        return actionFor("open");
    }

    public static Action quitAction() {
        return actionFor("quit");
    }

    public static Action aboutAction() {
        return actionFor("about");
    }

    public static Action preferencesAction() {
        return actionFor("preferences");
    }

    public static Action saveAction() {
        Action action = actionFor("save");
        action.setEnabled(false);
        return action;
    }

    public static Action saveAsAction() {
        Action action = actionFor("saveAs");
        action.setEnabled(false);
        return action;
    }

    public static Action undoAction() {
        Action action = actionFor("undo");
        action.setEnabled(false);
        return action;
    }

    public static Action redoAction() {
        Action action = actionFor("redo");
        action.setEnabled(false);
        return action;
    }

    public static Action cutAction() {
        Action action = actionFor("cut");
        action.setEnabled(false);
        return action;
    }

    public static Action copyAction() {
        Action action = actionFor("copy");
        action.setEnabled(false);
        return action;
    }

    public static Action pasteAction() {
        Action action = actionFor("paste");
        action.setEnabled(false);
        return action;
    }

    public static Action deleteAction() {
        return action(actionFor("delete"))
                    .withAccelerator(KeyStroke.getKeyStroke(message("application.action.Delete.shortcut", "DELETE")))
                    .withEnabled(false)
                    .build();
    }

    private static Action actionFor(String actionName) {
        return ActionManager.getInstance().actionFor(controller(), actionName);
    }

    private static @artifact.name.plain@Controller controller() {
        return (@artifact.name.plain@Controller) ApplicationHolder.getApplication().getMvcGroupManager().findGroup("@griffon.project.name@").getController();
    }

    private static String message(String key, String defaultValue) {
        return MessageSourceHolder.getMessageSource().getMessage(key, defaultValue);
    }
}
