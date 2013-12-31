import griffon.swing.SwingGriffonApplication;

public class Main {
    public static void main(String[] args) throws Exception {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");

        SwingGriffonApplication.run(SwingGriffonApplication.class, args);
    }
}
