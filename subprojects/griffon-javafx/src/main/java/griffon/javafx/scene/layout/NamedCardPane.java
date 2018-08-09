/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static griffon.util.GriffonClassUtils.requireState;
import static griffon.util.GriffonNameUtils.isBlank;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.11.0
 */
public class NamedCardPane extends StackPane {
    private static final String ERROR_NODE_NULL = "Argument 'node' must not be null";
    private static final String ERROR_ID_NULL = "Argument 'id' must not be null";

    private final Map<String, Node> nodes = new ConcurrentHashMap<>();
    private final StringProperty selectedNodeId = new SimpleStringProperty(this, "selectedNodeId");
    private final ObjectProperty<Node> selectedNode = new SimpleObjectProperty<>(this, "selectedNode");
    private final AtomicBoolean adjusting = new AtomicBoolean(false);

    public NamedCardPane() {
        getStyleClass().add("named-cardpane");

        getChildren().addListener((ListChangeListener<Node>) c -> {
            if (adjusting.get()) {
                return;
            }

            while (c.next()) {
                if (c.wasAdded()) {
                    Node lastAddedNode = null;
                    for (Node node : c.getAddedSubList()) {
                        String id = node.getId();
                        requireNonBlank(id, ERROR_ID_NULL);
                        if (!nodes.containsKey(id)) {
                            nodes.put(id, node);
                            lastAddedNode = node;
                        }
                    }

                    if (lastAddedNode != null) {
                        String nextId = lastAddedNode.getId();
                        doShow(nextId);
                    }

                } else if (c.wasRemoved()) {
                    String selectedId = getSelectedNodeId();
                    boolean selectionChanged = false;
                    for (Node node : c.getRemoved()) {
                        String id = node.getId();
                        requireNonBlank(id, ERROR_ID_NULL);
                        if (!nodes.containsKey(id)) {
                            continue;
                        }

                        nodes.remove(id);

                        if (id.equals(selectedId)) {
                            selectionChanged = true;
                        }
                    }

                    if (selectionChanged) {
                        final String nextId = nodes.size() > 0 ? nodes.keySet().iterator().next() : null;
                        doShow(nextId);
                    }
                }
            }
        });

        widthProperty().addListener((observable, oldValue, newValue) -> updateBoundsInChildren());
        heightProperty().addListener((observable, oldValue, newValue) -> updateBoundsInChildren());
    }

    protected void updateBoundsInChildren() {
        nodes.values().forEach(this::updateChildBounds);
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
    public ReadOnlyStringProperty selectedNodeIdProperty() {
        return selectedNodeId;
    }

    @Nonnull
    public ReadOnlyObjectProperty<Node> selectedNodeProperty() {
        return selectedNode;
    }

    @Nullable
    public String getSelectedNodeId() {
        return selectedNodeId.get();
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
        selectedNodeId.set(null);
        selectedNode.set(null);
    }

    public void add(@Nonnull final String id, @Nonnull final Node node) {
        requireNonNull(node, ERROR_NODE_NULL);
        requireNonBlank(id, ERROR_ID_NULL);

        adjusting.set(true);
        nodes.put(id, node);
        adjusting.set(false);

        show(id);
    }

    public void remove(@Nonnull String id) {
        requireNonBlank(id, ERROR_ID_NULL);
        if (!nodes.containsKey(id)) {
            return;
        }

        String selectedId = getSelectedNodeId();
        adjusting.set(true);
        nodes.remove(id);
        adjusting.set(false);

        if (id.equals(selectedId)) {
            String nextId = null;
            if (nodes.size() > 0) {
                nextId = nodes.keySet().iterator().next();
            }
            doShow(nextId);
        }
    }

    public void show(@Nonnull String id) {
        requireNonBlank(id, ERROR_ID_NULL);
        requireState(nodes.containsKey(id), "No content associated with id '" + id + "'");
        doShow(id);
    }

    protected void doShow(@Nullable String id) {
        Platform.runLater(() -> {
            if (isBlank(id)) {
                adjusting.set(true);
                getChildren().clear();
                adjusting.set(false);
                selectedNodeId.set(null);
                selectedNode.set(null);
            } else {
                Node node = nodes.get(id);
                adjusting.set(true);
                getChildren().setAll(node);
                adjusting.set(false);
                selectedNodeId.set(id);
                selectedNode.set(node);
            }
        });
    }
}
