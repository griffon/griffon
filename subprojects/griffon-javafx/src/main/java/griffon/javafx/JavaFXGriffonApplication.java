/*
 * Copyright 2009-2014 the original author or authors.
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

package griffon.javafx;

import javafx.application.Application;
import javafx.stage.Stage;
import org.codehaus.griffon.runtime.core.ApplicationBootstrapper;

import javax.annotation.Nonnull;

import static griffon.core.GriffonExceptionHandler.registerExceptionHandler;

/**
 * @author Dean Iverson
 * @author Andres Almiray
 */
public class JavaFXGriffonApplication extends AbstractJavaFXGriffonApplication {
    private boolean primaryStageDispensed = false;
    private Stage primaryStage;

    public JavaFXGriffonApplication() {
        this(EMPTY_ARGS);
    }

    public JavaFXGriffonApplication(String[] args) {
        super(args);
    }

    @Nonnull
    public Object createApplicationContainer() {
        if (primaryStageDispensed) {
            return new Stage();
        } else {
            primaryStageDispensed = true;
            return primaryStage;
        }
    }

    @Override
    public void init() throws Exception {
        ApplicationBootstrapper bootstrapper = new ApplicationBootstrapper(this);
        bootstrapper.bootstrap();
        initialize();
    }

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
        primaryStage = stage;

        getUIThreadManager().runOutsideUI(new Runnable() {
            @Override
            public void run() {
                startup();
                ready();
            }
        });
    }

    /*
    public void bootstrap() {
        initialize();
    }

    public void realize() {
        startup();
    }

    public void show() {
        Window startingWindow = getWindowManager().getStartingWindow();
        getWindowManager().show(startingWindow);
        ready();
    }
    */

    public boolean shutdown() {
        if (super.shutdown()) {
            exit();
        }
        return false;
    }

    public void exit() {
        System.exit(0);
    }

    public static void run(Class<? extends Application> applicationClass, String[] args) {
        registerExceptionHandler();
        Application.launch(applicationClass, args);
    }

    public static void main(String[] args) {
        run(JavaFXGriffonApplication.class, args);
    }
}
