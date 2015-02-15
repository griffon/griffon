package org.example.calculator;

import griffon.swing.SwingGriffonApplication;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class Launcher {
    public static void main(String[] args) throws Exception {
        SLF4JBridgeHandler.install();
        SwingGriffonApplication.main(args);
    }
}