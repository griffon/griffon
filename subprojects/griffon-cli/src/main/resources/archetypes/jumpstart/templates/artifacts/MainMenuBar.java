@artifact.package@import javax.swing.*;
import griffon.plugins.i18n.MessageSourceHolder;
import static griffon.util.GriffonApplicationUtils.*;

public class @artifact.name.plain@MenuBar {
    public static JMenuBar menuBar() {
        JMenuBar menuBar = new JMenuBar();

        fileMenu(menuBar);
        editMenu(menuBar);
        viewMenu(menuBar);
        helpMenu(menuBar);

        return menuBar;
    }

    private static void fileMenu(JMenuBar menuBar) {
        JMenu menu = new JMenu(message("application.menu.File.name", "File"));
        menu.setMnemonic(message("application.menu.File.mnemonic", "F").charAt(0));
        menu.add(new JMenuItem(@artifact.name.plain@Actions.newAction()));
        menu.add(new JMenuItem(@artifact.name.plain@Actions.openAction()));
        menu.addSeparator();
        menu.add(new JMenuItem(@artifact.name.plain@Actions.saveAction()));
        menu.add(new JMenuItem(@artifact.name.plain@Actions.saveAsAction()));
        if (!isMacOSX()) {
            menu.addSeparator();
            menu.add(new JMenuItem(@artifact.name.plain@Actions.quitAction()));
        }

        menuBar.add(menu);
    }

    private static void editMenu(JMenuBar menuBar) {
        JMenu menu = new JMenu(message("application.menu.Edit.name", "Edit"));
        menu.setMnemonic(message("application.menu.Edit.mnemonic", "E").charAt(0));
        menu.add(new JMenuItem(@artifact.name.plain@Actions.undoAction()));
        menu.add(new JMenuItem(@artifact.name.plain@Actions.redoAction()));
        menu.addSeparator();
        menu.add(new JMenuItem(@artifact.name.plain@Actions.cutAction()));
        menu.add(new JMenuItem(@artifact.name.plain@Actions.copyAction()));
        menu.add(new JMenuItem(@artifact.name.plain@Actions.pasteAction()));
        menu.add(new JMenuItem(@artifact.name.plain@Actions.deleteAction()));
        menuBar.add(menu);
    }

    private static void viewMenu(JMenuBar menuBar) {
        JMenu menu = new JMenu(message("application.menu.View.name", "View"));
        menu.setMnemonic(message("application.menu.View.mnemonic", "V").charAt(0));

        menuBar.add(menu);
    }

    private static void helpMenu(JMenuBar menuBar) {
        if (!isMacOSX()) {
            menuBar.add(Box.createGlue());
        }
        JMenu menu = new JMenu(message("application.menu.Help.name", "Help"));
        menu.setMnemonic(message("application.menu.Help.mnemonic", "H").charAt(0));
        if (!isMacOSX()) {
            menu.add(new JMenuItem(@artifact.name.plain@Actions.aboutAction()));
            menu.add(new JMenuItem(@artifact.name.plain@Actions.preferencesAction()));
        }

        menuBar.add(menu);
    }

    private static String message(String key, String defaultValue) {
        return MessageSourceHolder.getMessageSource().getMessage(key, defaultValue);
    }
}
