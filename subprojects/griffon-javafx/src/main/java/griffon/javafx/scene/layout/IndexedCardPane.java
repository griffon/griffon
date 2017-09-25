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
package griffon.javafx.scene.layout;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static griffon.util.GriffonClassUtils.requireState;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.11.0
 */
public class IndexedCardPane extends StackPane {
    private static final String ERROR_NODE_NULL = "Argument 'node' must not be null";

    private final List<Node> nodes = new ArrayList<>();
    private final IntegerProperty selectedIndex = new SimpleIntegerProperty(this, "selectedIndex", -1);
    private final ObjectProperty<Node> selectedNode = new SimpleObjectProperty<>(this, "selectedNode");
    private final AtomicBoolean adjusting = new AtomicBoolean(false);

    public IndexedCardPane() {
        getStyleClass().add("indexed-cardpane");

        getChildren().addListener((ListChangeListener<Node>) c -> {
            if (adjusting.get()) {
                return;
            }

            while (c.next()) {
                if (c.wasAdded()) {
                    Node lastAddedNode = null;
                    for (Node node : c.getAddedSubList()) {
                        if (!nodes.contains(node)) {
                            nodes.add(node);
                            lastAddedNode = node;
                        }
                    }

                    if (lastAddedNode != null) {
                        doShow(indexOf(lastAddedNode));
                    }
                } else if (c.wasRemoved()) {
                    int selectedIndex = getSelectedIndex();
                    for (Node node : c.getRemoved()) {
                        if (!nodes.contains(node)) {
                            continue;
                        }

                        int removedIndex = indexOf(node);
                        nodes.remove(node);

                        if (removedIndex <= selectedIndex) {
                            selectedIndex = -1;
                        }
                    }

                    doShow(selectedIndex);
                }
            }
        });

        widthProperty().addListener((observable, oldValue, newValue) -> updateBoundsInChildren());
        heightProperty().addListener((observable, oldValue, newValue) -> updateBoundsInChildren());
    }

    protected void updateBoundsInChildren() {
        for (Node node : nodes) {
            updateChildBounds(node);
        }
        layout();
    }

    protected void updateChildBounds(Node node) {
        if (node instanceof Region) {
            Region child = (Region) node;
            child.setPrefWidth(getWidth());
            child.setPrefHeight(getHeight());
        }
    }

    @Nonnull
    public ReadOnlyIntegerProperty selectedIndexProperty() {
        return selectedIndex;
    }

    @Nonnull
    public ReadOnlyObjectProperty<Node> selectedNodeProperty() {
        return selectedNode;
    }

    public int getSelectedIndex() {
        return selectedIndex.get();
    }

    @Nullable
    public Node getSelectedNode() {
        return selectedNode.get();
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    public int size() {
        return nodes.size();
    }

    public void clear() {
        nodes.clear();
        getChildren().clear();
        selectedIndex.set(-1);
        selectedNode.set(null);
    }

    public int indexOf(@Nonnull Node node) {
        requireNonNull(node, ERROR_NODE_NULL);
        return nodes.indexOf(node);
    }

    public void add(@Nonnull Node node) {
        requireNonNull(node, ERROR_NODE_NULL);
        if (!nodes.contains(node)) {
            adjusting.set(true);
            nodes.add(node);
            adjusting.set(false);
        }
        show(indexOf(node));
    }

    public void remove(@Nonnull Node node) {
        requireNonNull(node, ERROR_NODE_NULL);
        if (!nodes.contains(node)) {
            return;
        }

        int selectedIndex = getSelectedIndex();
        int removedIndex = indexOf(node);
        adjusting.set(true);
        nodes.remove(node);
        adjusting.set(false);

        if (removedIndex <= selectedIndex) {
            doShow(selectedIndex - 1);
        }
    }

    public void first() {
        if (!nodes.isEmpty()) {
            show(0);
        }
    }

    public void last() {
        int size = nodes.size();
        if (size > 0) {
            show(size - 1);
        }
    }

    public void next() {
        int size = nodes.size();
        if (size > 0) {
            int index = getSelectedIndex() + 1;
            show(index >= size ? 0 : index);
        }
    }

    public void previous() {
        int size = nodes.size();
        if (size > 0) {
            int index = getSelectedIndex() - 1;
            show(index < 0 ? size - 1 : index);
        }
    }

    public void show(int index) {
        requireState(index > -1, "Argument 'index' must be greater than -1");
        requireState(index < nodes.size(), "Argument 'index' must be less than " + nodes.size());
        doShow(index);
    }

    protected void doShow(final int index) {
        Platform.runLater(() -> {
            if (index < 0) {
                adjusting.set(true);
                getChildren().clear();
                adjusting.set(false);
                selectedIndex.set(-1);
                selectedNode.set(null);
            } else {
                Node node = nodes.get(index);
                adjusting.set(true);
                getChildren().setAll(node);
                adjusting.set(false);
                selectedIndex.set(index);
                selectedNode.set(node);
            }
        });
    }
}
