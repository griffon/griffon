package sample;

import griffon.lanterna.LanternaGriffonApplication;

public class Launcher {
    public static void main(String[] args) throws Exception {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");

        LanternaGriffonApplication.run(LanternaGriffonApplication.class, args);
    }
}
