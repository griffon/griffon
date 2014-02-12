package sample.lanterna.groovy

import griffon.lanterna.LanternaGriffonApplication

class Launcher {
    static void main(String[] args) throws Exception {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'trace')

        LanternaGriffonApplication.run(LanternaGriffonApplication, args)
    }
}
