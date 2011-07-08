@artifact.package@import javax.swing.*;
import griffon.util.ApplicationHolder;
import griffon.util.RunnableWithArgs;
import griffon.plugins.i18n.MessageSourceHolder;
import static griffon.swing.SwingAction.action;

public class @artifact.name.plain@Actions {
    public static Action newAction() {
        return action(message("application.action.New.name", "New"))
                .withMnemonic(message("application.action.New.mnemonic", "N"))
                .withAccelerator(message("application.action.New.shortcut", "N"))
                .withShortDescription(message("application.action.New.description", "New"))
                .withRunnable(new RunnableWithArgs() {
                    public void run(Object[] args) {
                        controller().newAction();
                    }
                }).build();
    }

    public static Action openAction() {
        return action(message("application.action.Open.name", "Open"))
                .withMnemonic(message("application.action.Open.mnemonic", "O"))
                .withAccelerator(message("application.action.Open.shortcut", "O"))
                .withShortDescription(message("application.action.Open.description", "Open"))
                .withRunnable(new RunnableWithArgs() {
                    public void run(Object[] args) {
                        controller().openAction();
                    }
                }).build();
    }

    public static Action quitAction() {
        return action(message("application.action.Quit.name", "Quit"))
                .withMnemonic(message("application.action.Quit.mnemonic", "Q"))
                .withAccelerator(message("application.action.Quit.shortcut", "Q"))
                .withShortDescription(message("application.action.Quit.description", "Quit"))
                .withRunnable(new RunnableWithArgs() {
                    public void run(Object[] args) {
                        controller().quitAction();
                    }
                }).build();
    }

    public static Action aboutAction() {
        return action(message("application.action.About.name", "About"))
                .withMnemonic(message("application.action.About.mnemonic", "B"))
                .withAccelerator(message("application.action.About.shortcut", "B"))
                .withShortDescription(message("application.action.About.description", "About"))
                .withRunnable(new RunnableWithArgs() {
                    public void run(Object[] args) {
                        controller().aboutAction();
                    }
                }).build();
    }

    public static Action preferencesAction() {
        return action(message("application.action.Preferences.name", "Preferences"))
                .withMnemonic(message("application.action.Preferences.mnemonic", "E"))
                .withAccelerator(message("application.action.Preferences.shortcut", "E"))
                .withShortDescription(message("application.action.Preferences.description", "Preferences"))
                .withRunnable(new RunnableWithArgs() {
                    public void run(Object[] args) {
                        controller().preferencesAction();
                    }
                }).build();
    }

    public static Action saveAction() {
        Action action = action(message("application.action.Save.name", "Save"))
                .withMnemonic(message("application.action.Save.mnemonic", "S"))
                .withAccelerator(message("application.action.Save.shortcut", "S"))
                .withShortDescription(message("application.action.Save.description", "Save"))
                .withRunnable(new RunnableWithArgs() {
                    public void run(Object[] args) {
                        controller().saveAction();
                    }
                }).build();
        action.setEnabled(false);
        return action;
    }

    public static Action saveAsAction() {
        Action action = action(message("application.action.SaveAs.name", "Save as..."))
                .withAccelerator(message("application.action.Save.shortcut", "shift S"))
                .withRunnable(new RunnableWithArgs() {
                    public void run(Object[] args) {
                        controller().saveAsAction();
                    }
                }).build();
        action.setEnabled(false);
        return action;
    }

    public static Action undoAction() {
        Action action = action(message("application.action.Undo.name", "Undo"))
                .withMnemonic(message("application.action.Undo.mnemonic", "Z"))
                .withAccelerator(message("application.action.Undo.shortcut", "Z"))
                .withShortDescription(message("application.action.Undo.description", "Undo"))
                .withRunnable(new RunnableWithArgs() {
                    public void run(Object[] args) {
                        controller().undoAction();
                    }
                }).build();
        action.setEnabled(false);
        return action;
    }

    public static Action redoAction() {
        Action action = action(message("application.action.Redo.name", "Redo"))
                .withMnemonic(message("application.action.Redo.mnemonic", "R"))
                .withAccelerator(message("application.action.Redo.shortcut", "shift Z"))
                .withShortDescription(message("application.action.Redo.description", "Redo"))
                .withRunnable(new RunnableWithArgs() {
                    public void run(Object[] args) {
                        controller().redoAction();
                    }
                }).build();
        action.setEnabled(false);
        return action;
    }

    public static Action cutAction() {
        Action action = action(message("application.action.Cut.name", "Cut"))
                .withMnemonic(message("application.action.Cut.mnemonic", "T"))
                .withAccelerator(message("application.action.Cut.shortcut", "X"))
                .withShortDescription(message("application.action.Cut.description", "Cut"))
                .withRunnable(new RunnableWithArgs() {
                    public void run(Object[] args) {
                        controller().cutAction();
                    }
                }).build();
        action.setEnabled(false);
        return action;
    }

    public static Action copyAction() {
        Action action = action(message("application.action.Copy.name", "Copy"))
                .withMnemonic(message("application.action.Copy.mnemonic", "C"))
                .withAccelerator(message("application.action.Copy.shortcut", "C"))
                .withShortDescription(message("application.action.Copy.description", "Copy"))
                .withRunnable(new RunnableWithArgs() {
                    public void run(Object[] args) {
                        controller().copyAction();
                    }
                }).build();
        action.setEnabled(false);
        return action;
    }

    public static Action pasteAction() {
        Action action = action(message("application.action.Paste.name", "Paste"))
                .withMnemonic(message("application.action.Paste.mnemonic", "P"))
                .withAccelerator(message("application.action.Paste.shortcut", "V"))
                .withShortDescription(message("application.action.Paste.description", "Paste"))
                .withRunnable(new RunnableWithArgs() {
                    public void run(Object[] args) {
                        controller().pasteAction();
                    }
                }).build();
        action.setEnabled(false);
        return action;
    }

    public static Action deleteAction() {
        Action action = action(message("application.action.Delete.name", "Delete"))
                .withMnemonic(message("application.action.Delete.mnemonic", "D"))
                .withShortDescription(message("application.action.Delete.description", "Delete"))
                .withRunnable(new RunnableWithArgs() {
                    public void run(Object[] args) {
                        controller().deleteAction();
                    }
                }).build();
        action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(message("application.action.Delete.shortcut", "DELETE")));
        action.setEnabled(false);
        return action;
    }

    private static @artifact.name.plain@Controller controller() {
        return (@artifact.name.plain@Controller) ApplicationHolder.getApplication().getGroups().get("@griffon.project.name@").get("controller");
    }

    private static String message(String key, String defaultValue) {
        return MessageSourceHolder.getMessageSource().getMessage(key, defaultValue);
    }
}
