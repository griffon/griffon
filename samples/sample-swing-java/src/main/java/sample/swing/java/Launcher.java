package sample.swing.java;

import griffon.swing.SwingGriffonApplication;

public class Launcher {
    public static void main(String[] args) throws Exception {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");

        SwingGriffonApplication.run(SwingGriffonApplication.class, args);
    }
}
