package sample;

import griffon.core.GriffonApplication;
import griffon.inject.DependsOn;
import org.codehaus.griffon.runtime.core.addon.AbstractGriffonAddon;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

@Named("application")
@DependsOn("swing")
public class ApplicationAddon extends AbstractGriffonAddon {
    @Inject
    public ApplicationAddon(@Nonnull GriffonApplication application) {
        super(application);
    }

    public void onStartupStart(GriffonApplication application) {
        System.out.println("Application is entering STARTUP phase");
    }
}
