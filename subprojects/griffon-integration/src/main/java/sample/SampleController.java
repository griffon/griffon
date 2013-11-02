package sample;

import griffon.core.GriffonApplication;
import griffon.transform.Threading;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class SampleController extends AbstractGriffonController {
    @Inject
    public SampleController(@Nonnull GriffonApplication application) {
        super(application);
    }

    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
    public void click() {
        System.out.println(getApplication().getUIThreadManager());
        System.out.println("isUIThread? " + isUIThread());
        System.out.println("FOO");
        runInsideUIAsync(new Runnable() {
            @Override
            public void run() {
                System.out.println("isUIThread? " + isUIThread());
                System.out.println("BAR");
            }
        });
    }
}
