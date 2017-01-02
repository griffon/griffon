/*
 * Copyright 2008-2017 the original author or authors.
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
package griffon.javafx.support

import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.scene.control.Accordion
import javafx.scene.control.Button
import javafx.scene.control.ButtonBar
import javafx.scene.control.ContextMenu
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.control.ScrollPane
import javafx.scene.control.SplitPane
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.TitledPane
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import spock.lang.Specification
import spock.lang.Unroll

import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch

@Unroll
class JavaFXUtilsSpec extends Specification {
    static {
        // initialize UI toolkit
        new JFXPanel()
    }

    void "Find the button on #container.class using findNode method"() {
        expect:
        JavaFXUtils.findNode(container, 'buttonId')

        where:
        container << [
            createBorderPane(),
            createTabPane(),
            createTitledPane(),
            createSplitPane(),
            createAccordion(),
            createScrollPane(),
            createToolBar()
        ]
    }

    void "Find the button on #container.class using findElement method"() {
        expect:
        JavaFXUtils.findElement(container, 'buttonId')

        where:
        container << [
            createBorderPane(),
            createTabPane(),
            createTitledPane(),
            createSplitPane(),
            createAccordion(),
            createScrollPane(),
            createToolBar(),
            createContextMenu(),
            createMenuBar(),
            createButtonBar()
        ]
    }

    void "Find the buttons on #container.class using findElements method"() {
        when:
        def result = JavaFXUtils.findElements(container) { e ->
            e.properties.tag == 'tag1'
        }

        then:
        result.size() == 1

        where:
        container << [
            createBorderPaneWithTags(),
            createTabPaneWithTags(),
            createTitledPaneWithTags(),
            createSplitPaneWithTags(),
            createAccordionWithTags(),
            createScrollPaneWithTags(),
            createToolBarWithTags(),
            createContextMenuWithTags(),
            createMenuBarWithTags(),
            createButtonBarWithTags()
        ]
    }

    private BorderPane createBorderPane() {
        runInsideUISync {
            BorderPane pane = new BorderPane()
            pane.children << createButton()
            pane
        }
    }

    private ContextMenu createContextMenu() {
        runInsideUISync {
            ContextMenu contextMenu = new ContextMenu()
            Menu primary = new Menu('Primary')
            Menu secondary = new Menu('Secondary')
            primary.items << secondary
            MenuItem item = new MenuItem()
            item.id = 'buttonId'
            secondary.items << item
            contextMenu.items << primary
            contextMenu
        }
    }

    private MenuBar createMenuBar() {
        runInsideUISync {
            MenuBar menuBar = new MenuBar()
            Menu primary = new Menu('Primary')
            Menu secondary = new Menu('Secondary')
            primary.items << secondary
            MenuItem item = new MenuItem()
            item.id = 'buttonId'
            secondary.items << item
            menuBar.menus << primary
            menuBar
        }
    }

    private ButtonBar createButtonBar() {
        runInsideUISync {
            ButtonBar buttonBar = new ButtonBar()
            buttonBar.buttons << new Button('Primary')
            buttonBar.buttons << new Button('Secondary')
            buttonBar.buttons[1].id = 'buttonId'
            buttonBar
        }
    }

    private TabPane createTabPane() {
        runInsideUISync {
            TabPane tabPane = new TabPane()
            tabPane.tabs << new Tab('Tab #1')
            Tab tab = new Tab('Tab #2')
            tab.content = createBorderPane()
            tabPane.tabs << tab
            tabPane
        }
    }

    private TitledPane createTitledPane() {
        runInsideUISync {
            TitledPane pane = new TitledPane()
            pane.content = createBorderPane()
            pane
        }
    }

    private SplitPane createSplitPane() {
        runInsideUISync {
            SplitPane pane = new SplitPane()
            pane.items << new Pane()
            pane.items << createBorderPane()
            pane
        }
    }

    private ScrollPane createScrollPane() {
        runInsideUISync {
            ScrollPane pane = new ScrollPane()
            pane.content = createBorderPane()
            pane
        }
    }

    private Accordion createAccordion() {
        runInsideUISync {
            Accordion accordion = new Accordion()
            accordion.panes << createTitledPane()
            accordion
        }
    }

    private ToolBar createToolBar() {
        runInsideUISync {
            ToolBar toolBar = new ToolBar()
            toolBar.items << createButton()
            toolBar
        }
    }

    private Button createButton() {
        Button button = new Button()
        button.id = 'buttonId'
        button
    }

    private BorderPane createBorderPaneWithTags() {
        runInsideUISync {
            BorderPane pane = new BorderPane()
            pane.children << createButton()
            pane.children << createButtonWithTag('tag1')
            pane.children << createButtonWithTag('tag2')
            pane
        }
    }

    private ContextMenu createContextMenuWithTags() {
        runInsideUISync {
            ContextMenu contextMenu = new ContextMenu()
            Menu primary = new Menu('Primary')
            Menu secondary = new Menu('Secondary')
            primary.items << secondary
            secondary.items << new MenuItem()
            MenuItem item = new MenuItem()
            item.properties.tag = 'tag1'
            secondary.items << item
            item = new MenuItem()
            item.properties.tag = 'tag2'
            secondary.items << item
            contextMenu.items << primary
            contextMenu
        }
    }

    private MenuBar createMenuBarWithTags() {
        runInsideUISync {
            MenuBar menuBar = new MenuBar()
            Menu primary = new Menu('Primary')
            Menu secondary = new Menu('Secondary')
            primary.items << secondary
            secondary.items << new MenuItem()
            MenuItem item = new MenuItem()
            item.properties.tag = 'tag1'
            secondary.items << item
            item = new MenuItem()
            item.properties.tag = 'tag2'
            secondary.items << item
            menuBar.menus << primary
            menuBar
        }
    }

    private ButtonBar createButtonBarWithTags() {
        runInsideUISync {
            ButtonBar buttonBar = new ButtonBar()
            buttonBar.buttons << createButton()
            buttonBar.buttons << createButtonWithTag('tag1')
            buttonBar.buttons << createButtonWithTag('tag2')
            buttonBar
        }
    }

    private TabPane createTabPaneWithTags() {
        runInsideUISync {
            TabPane tabPane = new TabPane()
            tabPane.tabs << new Tab('Tab #1')
            Tab tab = new Tab('Tab #2')
            tab.content = createBorderPaneWithTags()
            tabPane.tabs << tab
            tabPane
        }
    }

    private TitledPane createTitledPaneWithTags() {
        runInsideUISync {
            TitledPane pane = new TitledPane()
            pane.content = createBorderPaneWithTags()
            pane
        }
    }

    private SplitPane createSplitPaneWithTags() {
        runInsideUISync {
            SplitPane pane = new SplitPane()
            pane.items << new Pane()
            pane.items << createBorderPaneWithTags()
            pane
        }
    }

    private ScrollPane createScrollPaneWithTags() {
        runInsideUISync {
            ScrollPane pane = new ScrollPane()
            pane.content = createBorderPaneWithTags()
            pane
        }
    }

    private Accordion createAccordionWithTags() {
        runInsideUISync {
            Accordion accordion = new Accordion()
            accordion.panes << createTitledPaneWithTags()
            accordion
        }
    }

    private ToolBar createToolBarWithTags() {
        runInsideUISync {
            ToolBar toolBar = new ToolBar()
            toolBar.items << createButtonWithTag('tag1')
            toolBar.items << createButtonWithTag('tag2')
            toolBar
        }
    }

    private Button createButtonWithTag(String tag) {
        Button button = new Button()
        button.properties.tag = tag
        button
    }

    private static <T> T runInsideUISync(Callable<T> callable) {
        if (Platform.isFxApplicationThread()) {
            return callable.call()
        }

        T result = null
        CountDownLatch latch = new CountDownLatch(1)
        Platform.runLater {
            result = callable.call()
            latch.countDown()
        }
        latch.await()
        result
    }
}
