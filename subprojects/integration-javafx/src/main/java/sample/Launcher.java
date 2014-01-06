package sample;

import griffon.javafx.JavaFXGriffonApplication;

public class Launcher {
    public static void main(String[] args) throws Exception {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");

        JavaFXGriffonApplication.main(args);
    }
}
