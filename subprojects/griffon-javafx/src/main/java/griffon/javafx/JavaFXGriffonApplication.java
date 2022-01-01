/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2022 the original author or authors.
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
package griffon.javafx;

import griffon.annotations.core.Nonnull;
import griffon.core.ApplicationBootstrapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.codehaus.griffon.runtime.core.DefaultApplicationBootstrapper;

import java.util.Map;

import static griffon.core.GriffonExceptionHandler.registerExceptionHandler;

/**
 * @author Dean Iverson
 * @author Andres Almiray
 */
public class JavaFXGriffonApplication extends AbstractJavaFXGriffonApplication {
    protected Stage primaryStage;
    private boolean primaryStageDispensed = false;

    public JavaFXGriffonApplication() {
        this(EMPTY_ARGS);
    }

    public JavaFXGriffonApplication(@Nonnull String[] args) {
        super(args);
    }

    public static void run(Class<? extends Application> applicationClass, String[] args) {
        registerExceptionHandler();
        Application.launch(applicationClass, args);
    }

    public static void main(String[] args) {
        run(JavaFXGriffonApplication.class, args);
    }

    @Nonnull
    @Override
    public Object createApplicationContainer(@Nonnull Map<String, Object> attributes) {
        if (primaryStageDispensed) {
            return new Stage();
        } else {
            primaryStageDispensed = true;
            return primaryStage;
        }
    }

    @Override
    public void init() throws Exception {
        ApplicationBootstrapper bootstrapper = createApplicationBootstrapper();
        bootstrapper.bootstrap();
        afterInit();
    }

    protected void afterInit() {
        initialize();
    }

    @Nonnull
    protected ApplicationBootstrapper createApplicationBootstrapper() {
        return new DefaultApplicationBootstrapper(this);
    }

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
        primaryStage = stage;

        afterStart();
    }

    protected void afterStart() {
        getUIThreadManager().executeOutsideUI(() -> {
            startup();
            ready();
            afterReady();
        });
    }

    protected void afterReady() {
        // empty
    }

    @Override
    public boolean shutdown() {
        if (super.shutdown()) {
            exit();
        }
        return false;
    }

    public void exit() {
        Platform.exit();
        System.exit(0);
    }
}
