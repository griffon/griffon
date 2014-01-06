package sample

import griffon.javafx.JavaFXGriffonApplication

class Launcher {
    static void main(String[] args) throws Exception {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'trace')

        JavaFXGriffonApplication.run(JavaFXGriffonApplication, args)
    }
}
