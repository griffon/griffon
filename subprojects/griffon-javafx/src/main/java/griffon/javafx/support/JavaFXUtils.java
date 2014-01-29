/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package griffon.javafx.support;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URL;

import static griffon.util.GriffonNameUtils.isBlank;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public final class JavaFXUtils {
    private static final String ERROR_CONTROL_NULL = "Argument 'control' cannot be null";

    private JavaFXUtils() {

    }

    public static void configure(final @Nonnull ButtonBase control, final @Nonnull JavaFXAction action) {
        requireNonNull(control, "Argument 'control' cannot be null");
        requireNonNull(action, "Argument 'action' cannot be null");

        action.onActionProperty().addListener(new ChangeListener<EventHandler<ActionEvent>>() {
            @Override
            public void changed(ObservableValue<? extends EventHandler<ActionEvent>> observableValue, EventHandler<ActionEvent> oldValue, EventHandler<ActionEvent> newValue) {
                control.onActionProperty().set(newValue);
            }
        });
        control.onActionProperty().set(action.getOnAction());

        action.nameProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                control.textProperty().set(newValue);
            }
        });
        control.textProperty().set(action.getName());

        action.descriptionProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                setTooltip(control, newValue);
            }
        });
        setTooltip(control, action.getDescription());

        action.iconProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                setIcon(control, newValue);
            }
        });
        if (!isBlank(action.getIcon())) {
            setIcon(control, action.getIcon());
        }

        action.enabledProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                control.setDisable(!newValue);
            }
        });
        control.setDisable(!action.getEnabled());
    }

    public static void setTooltip(@Nonnull Control control, @Nullable String text) {
        if (isBlank(text)) {
            return;
        }
        requireNonNull(control, ERROR_CONTROL_NULL);

        Tooltip tooltip = control.tooltipProperty().get();
        if (tooltip == null) {
            tooltip = new Tooltip();
            control.tooltipProperty().set(tooltip);
        }
        tooltip.setText(text);
    }

    public static void setIcon(@Nonnull ButtonBase control, @Nonnull String iconUrl) {
        requireNonNull(control, ERROR_CONTROL_NULL);
        requireNonBlank(iconUrl, "Argument 'iconUrl' cannot be blank");

        URL resource = Thread.currentThread().getContextClassLoader().getResource(iconUrl);
        if (resource != null) {
            Image image = new Image(resource.toString());
            control.graphicProperty().set(new ImageView(image));
        }
    }

    @Nullable
    public static Node findNode(@Nonnull Node root, @Nonnull String id) {
        requireNonNull(root, "Argument 'root' cannot be null");
        requireNonBlank(id, "Argument 'id' cannot be blank");

        if (id.equals(root.getId())) return root;

        if (root instanceof Parent) {
            Parent parent = (Parent) root;
            for (Node child : parent.getChildrenUnmodifiable()) {
                Node found = findNode(child, id);
                if (found != null) return found;
            }
        }

        return null;
    }
}
