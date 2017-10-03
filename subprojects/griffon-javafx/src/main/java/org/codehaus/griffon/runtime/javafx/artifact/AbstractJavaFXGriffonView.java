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
package org.codehaus.griffon.runtime.javafx.artifact;

import griffon.core.artifact.GriffonClass;
import griffon.core.artifact.GriffonController;
import griffon.core.controller.Action;
import griffon.exceptions.GriffonException;
import griffon.javafx.artifact.JavaFXGriffonView;
import griffon.javafx.support.ActionMatcher;
import griffon.javafx.support.GriffonBuilderFactory;
import griffon.javafx.support.JavaFXAction;
import griffon.javafx.support.JavaFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;

import static griffon.util.ConfigUtils.stripFilenameExtension;
import static griffon.util.GriffonNameUtils.isNotBlank;
import static griffon.util.GriffonNameUtils.requireNonBlank;

/**
 * JavaFX-friendly implementation of the GriffonView interface.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public abstract class AbstractJavaFXGriffonView extends AbstractGriffonView implements JavaFXGriffonView {
    private static final String FXML_SUFFIX = ".fxml";

    @Inject
    protected ActionMatcher actionMatcher;

    public AbstractJavaFXGriffonView() {

    }

    @Nonnull
    protected Node loadFromFXML() {
        return loadFromFXML(resolveBasename());
    }

    @Nonnull
    protected Node loadFromFXML(@Nonnull String baseName) {
        requireNonBlank(baseName, "Argument 'baseName' must not be blank");
        if (baseName.endsWith(FXML_SUFFIX)) {
            baseName = stripFilenameExtension(baseName);
        }
        baseName = baseName.replace('.', '/');
        String viewName = baseName + FXML_SUFFIX;
        String styleName = baseName + ".css";

        URL viewResource = getResourceAsURL(viewName);
        if (viewResource == null) {
            throw new IllegalStateException("resource " + viewName + " not found");
        }

        FXMLLoader fxmlLoader = createFxmlLoader(viewResource);
        configureFxmlLoader(fxmlLoader);

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

    @Nonnull
    protected FXMLLoader createFxmlLoader(@Nonnull URL viewResource) {
        return new FXMLLoader(viewResource);
    }

    protected void configureFxmlLoader(@Nonnull FXMLLoader fxmlLoader) {
        fxmlLoader.setBuilderFactory(new GriffonBuilderFactory(getApplication(), getMvcGroup()));
        fxmlLoader.setResources(getApplication().getMessageSource().asResourceBundle());
        fxmlLoader.setClassLoader(getApplication().getApplicationClassLoader().get());
        fxmlLoader.setControllerFactory(klass -> getMvcGroup().getView());
    }

    @Nonnull
    protected String resolveBasename() {
        GriffonClass griffonClass = getGriffonClass();
        String packageName = griffonClass.getPackageName();
        String baseName = griffonClass.getLogicalPropertyName();
        if (isNotBlank(packageName)) {
            baseName = packageName + "." + baseName;
        }
        return baseName;
    }

    protected void connectActions(@Nonnull Object node, @Nonnull GriffonController controller) {
        JavaFXUtils.connectActions(node, controller, actionMatcher);
    }

    protected void connectMessageSource(@Nonnull Object node) {
        JavaFXUtils.connectMessageSource(node, getApplication());
    }

    @Nullable
    protected JavaFXAction toolkitActionFor(@Nonnull GriffonController controller, @Nonnull String actionName) {
        Action action = actionFor(controller, actionName);
        return action != null ? (JavaFXAction) action.getToolkitAction() : null;
    }
}