/*
 * Copyright 2008-2015 the original author or authors.
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
package griffon.javafx.support;

import griffon.core.artifact.GriffonController;
import griffon.core.controller.Action;
import griffon.core.controller.ActionManager;
import griffon.core.editors.ValueConversionException;
import griffon.exceptions.InstanceMethodInvocationException;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Control;
import javafx.scene.control.Labeled;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.stage.Window;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Map;

import static griffon.util.GriffonClassUtils.getGetterName;
import static griffon.util.GriffonClassUtils.getPropertyValue;
import static griffon.util.GriffonClassUtils.invokeExactInstanceMethod;
import static griffon.util.GriffonClassUtils.invokeInstanceMethod;
import static griffon.util.GriffonNameUtils.isBlank;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 */
public final class JavaFXUtils {
    private static final String ERROR_NODE_NULL = "Argument 'node' must not be null";
    private static final String ERROR_CONTROL_NULL = "Argument 'control' must not be null";
    private static final String ERROR_ACTION_NULL = "Argument 'action' must not be null";
    private static final String ERROR_ICON_BLANK = "Argument 'iconUrl' must not be blank";
    private static final String ERROR_ID_BLANK = "Argument 'id' must not be blank";
    private static final String ERROR_URL_BLANK = "Argument 'url' must not be blank";
    private static final String ERROR_ROOT_NULL = "Argument 'root' must not be null";
    private static final String ERROR_CONTROLLER_NULL = "Argument 'controller' must not be null";
    private static final String ACTION_TARGET_SUFFIX = "ActionTarget";
    private static final String PROPERTY_SUFFIX = "Property";

    private JavaFXUtils() {

    }

    @Nonnull
    @SuppressWarnings("ConstantConditions")
    public static <B> Property<?> extractProperty(@Nonnull B bean, @Nonnull String propertyName) {
        requireNonNull(bean, "Argument 'bean' must not be null");
        requireNonBlank(propertyName, "Argument 'propertyName' must not be null");

        if (!propertyName.endsWith(PROPERTY_SUFFIX)) {
            propertyName += PROPERTY_SUFFIX;
        }

        InstanceMethodInvocationException imie;
        try {
            // 1. try <columnName>Property() first
            return (Property<?>) invokeExactInstanceMethod(bean, propertyName);
        } catch (InstanceMethodInvocationException e) {
            imie = e;
        }

        // 2. fallback to get<columnName>Property()
        try {
            return (Property<?>) invokeExactInstanceMethod(bean, getGetterName(propertyName));
        } catch (InstanceMethodInvocationException e) {
            throw imie;
        }
    }

    public static void connectActions(@Nonnull Node node, @Nonnull GriffonController controller) {
        requireNonNull(node, ERROR_NODE_NULL);
        requireNonNull(controller, ERROR_CONTROLLER_NULL);
        ActionManager actionManager = controller.getApplication().getActionManager();
        for (Map.Entry<String, Action> e : actionManager.actionsFor(controller).entrySet()) {
            String actionTargetName = actionManager.normalizeName(e.getKey()) + ACTION_TARGET_SUFFIX;
            Object control = findElement(node, actionTargetName);
            if (control == null) continue;
            JavaFXAction action = (JavaFXAction) e.getValue().getToolkitAction();

            if (control instanceof ButtonBase) {
                configure(((ButtonBase) control), action);
            } else if (control instanceof MenuItem) {
                JavaFXUtils.configure(((MenuItem) control), action);
            } else if (control instanceof Node) {
                ((Node) control).addEventHandler(ActionEvent.ACTION, action.getOnAction());
            } else {
                // does it support the onAction property?
                try {
                    invokeInstanceMethod(control, "setOnAction", action.getOnAction());
                } catch (InstanceMethodInvocationException imie) {
                    // ignore
                }
            }
        }
    }

    public static void configure(final @Nonnull ButtonBase control, final @Nonnull JavaFXAction action) {
        requireNonNull(control, ERROR_CONTROL_NULL);
        requireNonNull(action, ERROR_ACTION_NULL);

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

        action.imageProperty().addListener(new ChangeListener<Image>() {
            @Override
            public void changed(ObservableValue<? extends Image> observableValue, Image oldValue, Image newValue) {
                setGraphic(control, newValue);
            }
        });
        if (!isNull(action.getImage())) {
            setGraphic(control, action.getImage());
        }

        action.graphicProperty().addListener(new ChangeListener<Node>() {
            @Override
            public void changed(ObservableValue<? extends Node> observableValue, Node oldValue, Node newValue) {
                setGraphic(control, newValue);
            }
        });
        if (!isNull(action.getGraphic())) {
            setGraphic(control, action.getGraphic());
        }

        action.enabledProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                control.setDisable(!newValue);
            }
        });
        control.setDisable(!action.getEnabled());
    }

    public static void configure(final @Nonnull MenuItem control, final @Nonnull JavaFXAction action) {
        requireNonNull(control, ERROR_CONTROL_NULL);
        requireNonNull(action, ERROR_ACTION_NULL);

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

        action.acceleratorProperty().addListener(new ChangeListener<KeyCombination>() {
            @Override
            public void changed(ObservableValue<? extends KeyCombination> observable, KeyCombination oldValue, KeyCombination newValue) {
                control.setAccelerator(newValue);
            }
        });
        control.setAccelerator(action.getAccelerator());
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

    public static void setIcon(@Nonnull Labeled control, @Nonnull String iconUrl) {
        requireNonNull(control, ERROR_CONTROL_NULL);
        requireNonBlank(iconUrl, ERROR_ICON_BLANK);

        Node graphicNode = resolveIcon(iconUrl);
        if (graphicNode != null) {
            control.graphicProperty().set(graphicNode);
        }
    }

    public static void setIcon(@Nonnull MenuItem control, @Nonnull String iconUrl) {
        requireNonNull(control, ERROR_CONTROL_NULL);
        requireNonBlank(iconUrl, ERROR_ICON_BLANK);

        Node graphicNode = resolveIcon(iconUrl);
        if (graphicNode != null) {
            control.graphicProperty().set(graphicNode);
        }
    }

    public static void setGraphic(@Nonnull Labeled control, @Nullable Image graphic) {
        requireNonNull(control, ERROR_CONTROL_NULL);

        if (graphic != null) {
            Node graphicNode = new ImageView(graphic);
            control.graphicProperty().set(graphicNode);
        } else {
            control.graphicProperty().set(null);
        }
    }

    public static void setGraphic(@Nonnull MenuItem control, @Nullable Image graphic) {
        requireNonNull(control, ERROR_CONTROL_NULL);

        if (graphic != null) {
            Node graphicNode = new ImageView(graphic);
            control.graphicProperty().set(graphicNode);
        } else {
            control.graphicProperty().set(null);
        }
    }

    public static void setGraphic(@Nonnull Labeled control, @Nullable Node graphic) {
        requireNonNull(control, ERROR_CONTROL_NULL);

        if (graphic != null) {
            control.graphicProperty().set(graphic);
        } else {
            control.graphicProperty().set(null);
        }
    }

    public static void setGraphic(@Nonnull MenuItem control, @Nullable Node graphic) {
        requireNonNull(control, ERROR_CONTROL_NULL);

        if (graphic != null) {
            control.graphicProperty().set(graphic);
        } else {
            control.graphicProperty().set(null);
        }
    }

    @Nullable
    public static Node resolveIcon(@Nonnull String iconUrl) {
        requireNonBlank(iconUrl, ERROR_URL_BLANK);

        if (iconUrl.contains("|")) {
            // assume classname|arg format
            return handleAsClassWithArg(iconUrl);
        } else {
            URL resource = Thread.currentThread().getContextClassLoader().getResource(iconUrl);
            if (resource != null) {
                return new ImageView(new Image(resource.toString()));
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Node handleAsClassWithArg(String str) {
        String[] args = str.split("\\|");
        if (args.length == 2) {
            Class<?> iconClass = null;
            try {
                iconClass = (Class<?>) JavaFXUtils.class.getClassLoader().loadClass(args[0]);
            } catch (ClassNotFoundException e) {
                throw illegalValue(str, Node.class, e);
            }

            Constructor<?> constructor = null;
            try {
                constructor = iconClass.getConstructor(String.class);
            } catch (NoSuchMethodException e) {
                throw illegalValue(str, Node.class, e);
            }

            try {
                Object o = constructor.newInstance(args[1]);
                if (o instanceof Node) {
                    return (Node) o;
                } else if (o instanceof Image) {
                    return new ImageView((Image) o);
                } else {
                    throw illegalValue(str, Node.class);
                }
            } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
                throw illegalValue(str, Node.class, e);
            }
        } else {
            throw illegalValue(str, Node.class);
        }
    }

    @Nullable
    public static Node findNode(@Nonnull Node root, @Nonnull String id) {
        requireNonNull(root, ERROR_ROOT_NULL);
        requireNonBlank(id, ERROR_ID_BLANK);

        if (id.equals(root.getId())) return root;

        if (root instanceof TabPane) {
            TabPane parent = (TabPane) root;
            for (Tab child : parent.getTabs()) {
                if (child.getContent() != null) {
                    Node found = findNode(child.getContent(), id);
                    if (found != null) return found;
                }
            }
        } else if (root instanceof TitledPane) {
            TitledPane parent = (TitledPane) root;
            Node found = findNode(parent.getContent(), id);
            if (found != null) return found;
        } else if (root instanceof Accordion) {
            Accordion parent = (Accordion) root;
            for (TitledPane child : parent.getPanes()) {
                Node found = findNode(child, id);
                if (found != null) return found;
            }
        } else if (root instanceof SplitPane) {
            SplitPane parent = (SplitPane) root;
            for (Node child : parent.getItems()) {
                Node found = findNode(child, id);
                if (found != null) return found;
            }
        } else if (root instanceof ScrollPane) {
            ScrollPane scrollPane = (ScrollPane) root;
            Node found = findNode(scrollPane.getContent(), id);
            if (found != null) return found;
        } else if (root instanceof ToolBar) {
            ToolBar toolBar = (ToolBar) root;
            for (Node child : toolBar.getItems()) {
                Node found = findNode(child, id);
                if (found != null) return found;
            }
        } else if (root instanceof Parent) {
            Parent parent = (Parent) root;
            for (Node child : parent.getChildrenUnmodifiable()) {
                Node found = findNode(child, id);
                if (found != null) return found;
            }
        }

        return null;
    }

    @Nullable
    public static Object findElement(@Nonnull Object root, @Nonnull String id) {
        requireNonNull(root, ERROR_ROOT_NULL);
        requireNonBlank(id, ERROR_ID_BLANK);

        if (id.equals(getPropertyValue(root, "id"))) return root;

        if (root instanceof MenuBar) {
            MenuBar menuBar = (MenuBar) root;
            for (Menu child : menuBar.getMenus()) {
                Object found = findElement(child, id);
                if (found != null) return found;
            }
        } else if (root instanceof Menu) {
            Menu menu = (Menu) root;
            for (MenuItem child : menu.getItems()) {
                Object found = findElement(child, id);
                if (found != null) return found;
            }
        } else if (root instanceof TabPane) {
            TabPane tabPane = (TabPane) root;
            for (Tab child : tabPane.getTabs()) {
                Object found = findElement(child, id);
                if (found != null) return found;
            }
        } else if (root instanceof Tab) {
            Tab tab = (Tab) root;
            if (tab.getContent() != null) {
                Object found = findElement(tab.getContent(), id);
                if (found != null) return found;
            }
        } else if (root instanceof TitledPane) {
            TitledPane parent = (TitledPane) root;
            Object found = findElement(parent.getContent(), id);
            if (found != null) return found;
        } else if (root instanceof Accordion) {
            Accordion parent = (Accordion) root;
            for (TitledPane child : parent.getPanes()) {
                Object found = findElement(child, id);
                if (found != null) return found;
            }
        } else if (root instanceof SplitPane) {
            SplitPane parent = (SplitPane) root;
            for (Node child : parent.getItems()) {
                Object found = findElement(child, id);
                if (found != null) return found;
            }
        } else if (root instanceof ScrollPane) {
            ScrollPane scrollPane = (ScrollPane) root;
            Object found = findElement(scrollPane.getContent(), id);
            if (found != null) return found;
        } else if (root instanceof ToolBar) {
            ToolBar toolBar = (ToolBar) root;
            for (Node child : toolBar.getItems()) {
                Node found = findNode(child, id);
                if (found != null) return found;
            }
        } else if (root instanceof Parent) {
            Parent parent = (Parent) root;
            for (Node child : parent.getChildrenUnmodifiable()) {
                Object found = findElement(child, id);
                if (found != null) return found;
            }
        }

        return null;
    }

    @Nullable
    public static Window getWindowAncestor(@Nonnull Object node) {
        requireNonNull(node, ERROR_NODE_NULL);

        if (node instanceof Window) {
            return (Window) node;
        } else if (node instanceof Scene) {
            return ((Scene) node).getWindow();
        } else if (node instanceof Node) {
            Scene scene = ((Node) node).getScene();
            if (scene != null) {
                return scene.getWindow();
            }
        } else if (node instanceof Tab) {
            TabPane tabPane = ((Tab) node).getTabPane();
            if (tabPane != null) {
                return getWindowAncestor(tabPane);
            }
        }

        return null;
    }

    private static ValueConversionException illegalValue(Object value, Class<?> klass) {
        throw new ValueConversionException(value, klass);
    }

    private static ValueConversionException illegalValue(Object value, Class<?> klass, Exception e) {
        throw new ValueConversionException(value, klass, e);
    }
}
