package griffon.pivot.events;

import griffon.annotations.core.Nonnull;
import griffon.core.GriffonApplication;
import griffon.core.event.Event;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class AppResumeEvent extends Event {
    private final GriffonApplication application;

    @Nonnull
    public static AppResumeEvent of(@Nonnull GriffonApplication application) {
        return new AppResumeEvent(application);
    }

    public AppResumeEvent(@Nonnull GriffonApplication application) {
        this.application = requireNonNull(application, "Argument 'application' must not be null");
    }

    @Nonnull
    public GriffonApplication getApplication() {
        return application;
    }
}
