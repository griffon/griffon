package org.example;

import griffon.javafx.JavaFXGriffonApplication;

public class Launcher {
    public static void main(String[] args) throws Exception {
        System.setProperty("griffon.full.stacktrace", "true");
        JavaFXGriffonApplication.main(args);
    }
}