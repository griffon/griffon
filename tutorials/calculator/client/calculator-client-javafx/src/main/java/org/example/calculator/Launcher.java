package org.example.calculator;

import griffon.javafx.JavaFXGriffonApplication;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class Launcher {
    public static void main(String[] args) throws Exception {
        SLF4JBridgeHandler.install();
        JavaFXGriffonApplication.main(args);
    }
}