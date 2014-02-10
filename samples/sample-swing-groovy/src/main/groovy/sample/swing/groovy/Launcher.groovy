package sample.swing.groovy

import griffon.swing.SwingGriffonApplication

class Launcher {
    static void main(String[] args) throws Exception {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'trace')

        SwingGriffonApplication.run(SwingGriffonApplication, args)
    }
}
