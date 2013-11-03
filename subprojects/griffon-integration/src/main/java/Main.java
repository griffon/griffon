import griffon.core.CallableWithArgs;
import griffon.core.event.EventRouter;
import griffon.swing.SwingGriffonApplication;
import org.codehaus.griffon.runtime.core.ApplicationBootstrapper;

import javax.annotation.Nonnull;
import java.util.Arrays;

import static java.util.Arrays.asList;

public class Main {
    public static void main(String[] args) throws Exception {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace");

        final SwingGriffonApplication application = new SwingGriffonApplication(args);

        ApplicationBootstrapper bootstrapper = new ApplicationBootstrapper(application);
        bootstrapper.bootstrap();

        bootstrapper.run();

        EventRouter eventRouter = application.getInjector().getInstance(EventRouter.class);
        eventRouter.addEventListener("Foo", new CallableWithArgs<Void>() {
            @Override
            public Void call(@Nonnull Object[] args) {
                System.out.println(Arrays.toString(args));
                return null;
            }
        });
        eventRouter.publish("Foo", asList(1, 2, 3));

        /*
        final SampleController controller = application.getArtifactManager().newInstance(SampleController.class, GriffonControllerClass.TYPE);
        System.out.println(controller);
        application.getUIThreadManager().runInsideUISync(new Runnable() {
            @Override
            public void run() {
                application.getActionManager().invokeAction(controller, "click");
            }
        });
        */

        //application.shutdown();
    }
}
