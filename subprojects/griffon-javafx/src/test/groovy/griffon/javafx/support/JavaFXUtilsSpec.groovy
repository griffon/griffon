/*
 * Copyright 2008-2016 the original author or authors.
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
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.embed.swing.JFXPanel
import javafx.scene.control.Accordion
import javafx.scene.control.Button
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

import static com.jayway.awaitility.Awaitility.await

@Unroll
class JavaFXUtilsSpec extends Specification {
    static {
        // initialize UI toolkit
        new JFXPanel()
    }

    void "Trigger list events inside UI thread"() {
        given:
        ObservableList<String> source = FXCollections.observableArrayList()
        ObservableList<String> target = JavaFXUtils.createJavaFXThreadProxyList(source)
        ListChangeListener<String> witness = new ListChangeListener<String>() {
            boolean changed
            boolean changedInsideUIThread

            @Override
            void onChanged(ListChangeListener.Change<? extends String> c) {
                changed = true
                changedInsideUIThread = Platform.isFxApplicationThread()
            }
        }
        target.addListener(witness)

        when:
        source << 'change'
        await().until { witness.changed }

        then:
        witness.changedInsideUIThread
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
            createMenuBar()
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
        ContextMenu contextMenu = new ContextMenu()
        Menu primary = new Menu('Primary')
        Menu secondary = new Menu('Secondary')
        primary.items << secondary
        MenuItem button = new MenuItem()
        button.id = 'buttonId'
        secondary.items << button
        contextMenu.items << primary
        contextMenu
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar()
        Menu primary = new Menu('Primary')
        Menu secondary = new Menu('Secondary')
        primary.items << secondary
        MenuItem button = new MenuItem()
        button.id = 'buttonId'
        secondary.items << button
        menuBar.menus << primary
        menuBar
    }

    private TabPane createTabPane() {
        TabPane tabPane = new TabPane()
        tabPane.tabs << new Tab('Tab #1')
        Tab tab = new Tab('Tab #2')
        tab.content = createBorderPane()
        tabPane.tabs << tab
        tabPane
    }

    private TitledPane createTitledPane() {
        TitledPane pane = new TitledPane()
        pane.content = createBorderPane()
        pane
    }

    private SplitPane createSplitPane() {
        SplitPane pane = new SplitPane()
        pane.items << new Pane()
        pane.items << createBorderPane()
        pane
    }

    private ScrollPane createScrollPane() {
        ScrollPane pane = new ScrollPane()
        pane.content = createBorderPane()
        pane
    }

    private Accordion createAccordion() {
        Accordion accordion = new Accordion()
        accordion.panes << createTitledPane()
        accordion
    }

    private ToolBar createToolBar() {
        ToolBar toolBar = new ToolBar()
        toolBar.items << createButton()
        toolBar
    }

    private Button createButton() {
        Button button = new Button()
        button.id = 'buttonId'
        button
    }

    private <T> T runInsideUISync(Callable<T> callable) {
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
