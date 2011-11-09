@artifact.package@import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.AbstractTableComparatorChooser;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import griffon.core.MVCClosure;
import griffon.swing.BindUtils;
import griffon.util.GriffonNameUtils;
import griffon.util.Metadata;
import griffon.util.RunnableWithArgs;
import griffon.util.ConfigUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

import net.miginfocom.swing.MigLayout;
import griffon.plugins.actions.ActionManager;
import static griffon.swing.SwingAction.action;

public class AboutView extends AbstractDialogView {
    protected JComponent buildContent() {
        int spanCount = 1;
        Action creditsAction = null;
        Action licenseAction = null;

        Action hideAction = action(ActionManager.getInstance().actionFor(controller, "hideAction"))
                .withName(message("application.action.Close.name", "Close"))
                .withMnemonic(message("application.action.Close.mnemonic", "C"))
                .withShortDescription(message("application.action.Close.short_description", "Close"))
                .build();

        if (aboutModel().isIncludeCredits()) {
            spanCount++;
            creditsAction = action(message("application.action.Credits.name", "Credits"))
                    .withMnemonic(message("application.action.Credits.mnemonic", "R"))
                    .withShortDescription(message("application.action.Credits.short_description", "Credits"))
                    .withRunnable(new RunnableWithArgs() {
                        public void run(Object[] args) {
                            withMVCGroup("credits", new MVCClosure<CreditsModel, CreditsView, DialogController>() {
                                public void call(CreditsModel m, CreditsView v, DialogController c) {
                                    Window window = SwingUtilities.windowForComponent(content);
                                    c.show(window);
                                }
                            });
                        }

                    }).build();
        }

        if (aboutModel().isIncludeLicense()) {
            spanCount++;
            licenseAction = action(message("application.action.License.name", "License"))
                    .withMnemonic(message("application.action.License.mnemonic", "R"))
                    .withShortDescription(message("application.action.License.short_description", "License"))
                    .withRunnable(new RunnableWithArgs() {
                        public void run(Object[] args) {
                            withMVCGroup("license", new MVCClosure<LicenseModel, LicenseView, DialogController>() {
                                public void call(LicenseModel m, LicenseView v, DialogController c) {
                                    Window window = SwingUtilities.windowForComponent(content);
                                    c.show(window);
                                }
                            });
                        }

                    }).build();
        }

        String rowConstraints = "center, span " + spanCount + ", wrap";
        MigLayout layout = new MigLayout();
        layout.setLayoutConstraints("fill");
        JPanel panel = new JPanel(layout);

        JLabel label = new JLabel();
        label.setIcon(new ImageIcon(getImage("/griffon-icon-64x64.png")));
        panel.add(label, rowConstraints);

        label = new JLabel(GriffonNameUtils.capitalize(message("application.title",
                ConfigUtils.getConfigValue(getApp().getConfig(), "application.title").toString() + " " + Metadata.getCurrent().getApplicationVersion())));
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        panel.add(label, rowConstraints);

        label = new JLabel();
        BindUtils.binding()
                .withSource(model)
                .withSourceProperty("description")
                .withTarget(label)
                .withTargetProperty("text")
                .make(getBuilder());
        panel.add(label, rowConstraints);

        EventTableModel<Map<String, String>> tableModel = new EventTableModel<Map<String, String>>(aboutModel().getPlugins(), new TableFormat<Map<String, String>>() {
            private final String[] NAMES = {"Name", "Version"};

            public int getColumnCount() {
                return NAMES.length;
            }

            public String getColumnName(int i) {
                return NAMES[i];
            }

            public Object getColumnValue(Map<String, String> element, int i) {
                return element.get(GriffonNameUtils.uncapitalize(NAMES[i]));
            }
        });
        JTable table = new JTable(tableModel);
        TableComparatorChooser.install(table, (SortedList<Object>) aboutModel().getPlugins(), AbstractTableComparatorChooser.SINGLE_COLUMN);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(320,160));
        panel.add(scrollPane, rowConstraints);

        if (aboutModel().isIncludeCredits()) {
            panel.add(new JButton(creditsAction), "left");
        }
        if (aboutModel().isIncludeLicense()) {
            panel.add(new JButton(licenseAction), aboutModel().isIncludeCredits() ? "center" : "left");
        }
        panel.add(new JButton(hideAction), "right");

        String actionKey = "CloseAction";
        panel.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ESCAPE"), actionKey);
        panel.getActionMap().put(actionKey, hideAction);

        return panel;
    }

    private AboutModel aboutModel() {
        return (AboutModel) model;
    }
}
