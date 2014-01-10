package sample

import griffon.pivot.DesktopPivotGriffonApplication

class Launcher {
    static void main(String[] args) throws Exception {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'trace')

        DesktopPivotGriffonApplication.run(args)
    }
}
