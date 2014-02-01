/*
 * Copyright 2008-2014 the original author or authors.
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
package org.codehaus.griffon.runtime.core.artifact;

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonClass;
import griffon.core.artifact.GriffonController;
import griffon.core.controller.Action;
import griffon.core.controller.ActionManager;
import griffon.exceptions.GriffonException;
import griffon.javafx.support.JavaFXAction;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.util.Callback;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import static griffon.javafx.support.JavaFXUtils.findNode;
import static griffon.util.GriffonNameUtils.isBlank;

/**
 * JavaFX-friendly implementation of the GriffonView interface.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractJavaFXGriffonView extends AbstractGriffonView {
    private static final String ACTION_TARGET_SUFFIX = "ActionTarget";

    @Inject
    public AbstractJavaFXGriffonView(@Nonnull GriffonApplication application) {
        super(application);
    }

    @Nullable
    protected Node loadFromFXML() {
        String baseName = resolveBasename();
        baseName = baseName.replace('.', '/');
        String viewName = baseName + ".fxml";
        String styleName = baseName + ".css";

        URL viewResource = getResourceAsURL(viewName);
        if (viewResource == null) {
            return null;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(viewResource);
        fxmlLoader.setResources(getApplication().getMessageSource().asResourceBundle());
        fxmlLoader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> aClass) {
                return getMvcGroup().getView();
            }
        });

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new GriffonException(e);
        }

        Parent node = fxmlLoader.getRoot();

        URL cssResource = getResourceAsURL(styleName);
        if (cssResource != null) {
            String uriToCss = cssResource.toExternalForm();
            node.getStylesheets().add(uriToCss);
        }

        return node;
    }

    protected String resolveBasename() {
        GriffonClass griffonClass = getGriffonClass();
        String packageName = griffonClass.getPackageName();
        String baseName = griffonClass.getLogicalPropertyName();
        if (!isBlank(packageName)) {
            baseName = packageName + "." + baseName;
        }
        return baseName;
    }

    protected void connectActions(@Nonnull Node node, @Nonnull GriffonController controller) {
        ActionManager actionManager = getApplication().getActionManager();
        for (Map.Entry<String, Action> e : actionManager.actionsFor(controller).entrySet()) {
            String actionTargetName = actionManager.normalizeName(e.getKey()) + ACTION_TARGET_SUFFIX;
            Node control = findNode(node, actionTargetName);
            if (control == null) continue;
            JavaFXAction action = (JavaFXAction) e.getValue().getToolkitAction();
            control.addEventHandler(ActionEvent.ACTION, action.getOnAction());
        }
    }
}
