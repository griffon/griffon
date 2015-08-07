package ${project_package};

import griffon.javafx.JavaFXGriffonApplication;

public class Launcher extends JavaFXGriffonApplication {
    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");
    }
}
