/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package editor;

import griffon.core.artifact.GriffonView;
import griffon.core.controller.Action;
import griffon.inject.MVCMember;
import griffon.metadata.ArtifactProviderFor;
import org.codehaus.griffon.runtime.swing.artifact.AbstractSwingGriffonView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.File;
import java.util.Collections;
import java.util.Map;

import static griffon.util.GriffonApplicationUtils.isMacOSX;
import static java.util.Arrays.asList;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

@ArtifactProviderFor(GriffonView.class)
public class ContainerView extends AbstractSwingGriffonView {
    @MVCMember @Nonnull
    private ContainerModel model;
    @MVCMember @Nonnull
    private ContainerController controller;

    private JTabbedPane tabGroup;
    private JFileChooser fileChooser;

    public JTabbedPane getTabGroup() {
        return tabGroup;
    }

    @Override
    public void initUI() {
        JFrame window = (JFrame) getApplication()
            .createApplicationContainer(Collections.emptyMap());
        window.setName("mainWindow");
        window.setTitle(getApplication().getConfiguration().getAsString("application.title"));
        window.setSize(480, 320);
        window.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        window.setIconImage(getImage("/griffon-icon-48x48.png"));
        window.setIconImages(asList(
            getImage("/griffon-icon-48x48.png"),
            getImage("/griffon-icon-32x32.png"),
            getImage("/griffon-icon-16x16.png")
        ));
        getApplication().getWindowManager().attach("mainWindow", window);

        fileChooser = new JFileChooser();

        Map<String, Action> actionMap = getApplication().getActionManager().actionsFor(controller);
        Action saveAction = actionMap.get("save");
        model.getDocumentModel().addPropertyChangeListener("dirty", (e) -> saveAction.setEnabled((Boolean) e.getNewValue()));

        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new JMenuItem((javax.swing.Action) actionMap.get("open").getToolkitAction()));
        fileMenu.add(new JMenuItem((javax.swing.Action) actionMap.get("close").getToolkitAction()));
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem((javax.swing.Action) actionMap.get("save").getToolkitAction()));
        if (!isMacOSX()) {
            fileMenu.addSeparator();
            fileMenu.add(new JMenuItem((javax.swing.Action) actionMap.get("quit").getToolkitAction()));
        }
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        window.setJMenuBar(menuBar);

        window.getContentPane().setLayout(new BorderLayout());
        tabGroup = new JTabbedPane();
        tabGroup.addChangeListener(e -> {
            JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
            int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex < 0) {
                model.setMvcIdentifier(null);
            } else {
                JComponent tab = (JComponent) tabbedPane.getComponentAt(selectedIndex);
                model.setMvcIdentifier((String) tab.getClientProperty(ContainerModel.MVC_IDENTIFIER));
            }
        });
        window.getContentPane().add(tabGroup, BorderLayout.CENTER);
    }

    @Nullable
    public File selectFile() {
        Window window = (Window) getApplication().getWindowManager().getStartingWindow();
        int result = fileChooser.showOpenDialog(window);
        if (JFileChooser.APPROVE_OPTION == result) {
            return new File(fileChooser.getSelectedFile().toString());
        }
        return null;
    }

    private Image getImage(String path) {
        return Toolkit.getDefaultToolkit().getImage(ContainerView.class.getResource(path));
    }
}