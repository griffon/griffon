package sample.pivot.java;

import griffon.pivot.DesktopPivotGriffonApplication;

public class Launcher {
    public static void main(String[] args) throws Exception {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");

        DesktopPivotGriffonApplication.run(args);
    }
}
