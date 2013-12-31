import griffon.javafx.JavaFXGriffonApplication

class Main {
    static void main(String[] args) throws Exception {
        System.setProperty('org.slf4j.simpleLogger.defaultLogLevel', 'trace')

        JavaFXGriffonApplication.run(JavaFXGriffonApplication, args)
    }
}
