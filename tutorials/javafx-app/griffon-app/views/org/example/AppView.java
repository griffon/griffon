/*
 * Copyright 2016-2018 the original author or authors.
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
package org.example;

import griffon.core.artifact.GriffonView;
import org.kordamp.jipsy.ServiceProviderFor;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import org.codehaus.griffon.runtime.javafx.artifact.AbstractJavaFXGriffonView;

import griffon.annotations.core.Nonnull;
import java.util.Collections;
import java.util.Map;

@ServiceProviderFor(GriffonView.class)
public class AppView extends AbstractJavaFXGriffonView {
    private TabPane tabPane;

    @Nonnull
    public TabPane getTabPane() {
        return tabPane;
    }

    @Override
    public void mvcGroupInit(@Nonnull Map<String, Object> args) {
        createMVCGroup("tab1");
        createMVCGroup("tab2");
        createMVCGroup("tab3");
        createMVCGroup("tab4");
    }

    @Override
    public void initUI() {
        Stage stage = (Stage) getApplication()
            .createApplicationContainer(Collections.<String, Object>emptyMap());
        stage.setTitle(getApplication().getConfiguration().getAsString("application.title"));
        tabPane = new TabPane();
        Scene scene = new Scene(tabPane);
        scene.getStylesheets().add("org/kordamp/bootstrapfx/bootstrapfx.css");
        stage.setScene(scene);
        stage.sizeToScene();
        getApplication().getWindowManager().attach("mainWindow", stage);
    }
}
