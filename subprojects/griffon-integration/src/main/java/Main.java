import griffon.swing.SwingGriffonApplication;
import org.codehaus.griffon.runtime.core.ApplicationBootstrapper;

public class Main {
    public static void main(String[] args) throws Exception {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");

        final SwingGriffonApplication application = new SwingGriffonApplication(args);

        ApplicationBootstrapper bootstrapper = new ApplicationBootstrapper(application);
        bootstrapper.bootstrap();

        bootstrapper.run();

        application.shutdown();
    }
}
