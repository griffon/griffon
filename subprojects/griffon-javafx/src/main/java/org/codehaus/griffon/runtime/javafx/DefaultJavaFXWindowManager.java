package org.codehaus.griffon.runtime.javafx;

import griffon.core.ApplicationEvent;
import griffon.core.GriffonApplication;
import griffon.core.env.ApplicationPhase;
import griffon.core.event.EventRouter;
import griffon.exceptions.InstanceNotFoundException;
import griffon.javafx.JavaFXWindowDisplayHandler;
import griffon.javafx.JavaFXWindowManager;
import javafx.event.EventHandler;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.codehaus.griffon.runtime.core.view.AbstractWindowManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class DefaultJavaFXWindowManager extends AbstractWindowManager<Window> implements JavaFXWindowManager {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultJavaFXWindowManager.class);
    private final OnWindowHidingHelper onWindowHiding = new OnWindowHidingHelper();
    private final OnWindowShownHelper onWindowShown = new OnWindowShownHelper();
    private final OnWindowHiddenHelper onWindowHidden = new OnWindowHiddenHelper();

    @Inject
    @Nonnull
    public DefaultJavaFXWindowManager(@Nonnull GriffonApplication application, @Nonnull @Named("windowDisplayHandler") JavaFXWindowDisplayHandler windowDisplayHandler) {
        super(application, windowDisplayHandler);
        requireNonNull(application.getEventRouter(), "Argument 'application.eventRouter' cannot be null");
    }

    @Nonnull
    protected JavaFXWindowDisplayHandler resolveJavaFXWindowDisplayHandler() {
        return (JavaFXWindowDisplayHandler) resolveWindowDisplayHandler();
    }

    @Override
    protected void doAttach(@Nonnull Window window) {
        requireNonNull(window, ERROR_WINDOW_NULL);
        window.setOnHiding(onWindowHiding);
        window.setOnShown(onWindowShown);
        window.setOnHidden(onWindowHidden);
    }

    @Override
    protected void doDetach(@Nonnull Window window) {
        requireNonNull(window, ERROR_WINDOW_NULL);
        window.setOnHiding(null);
        window.setOnShown(null);
        window.setOnHidden(null);
    }

    @Override
    protected boolean isWindowVisible(@Nonnull Window window) {
        requireNonNull(window, ERROR_WINDOW_NULL);
        return window.isShowing();
    }

    public void handleClose(@Nonnull Window widget) {
        if (getApplication().getPhase() == ApplicationPhase.SHUTDOWN) {
            return;
        }
        int visibleWindows = 0;
        for (Window window : getWindows()) {
            if (window.isShowing()) {
                visibleWindows++;
            }
        }

        if (visibleWindows <= 1 && isAutoShutdown()) {
            if (!getApplication().shutdown())
                show(widget);
        }
    }

    /**
     * WindowAdapter that invokes hide() when the window is about to be closed.
     *
     * @author Andres Almiray
     */
    private class OnWindowHidingHelper implements EventHandler<WindowEvent> {
        public void handle(WindowEvent event) {
            hide((Window) event.getSource());
            handleClose((Window) event.getSource());
        }
    }

    /**
     * Listener that triggers application events when a window is shown.
     *
     * @author Andres Almiray
     */
    private class OnWindowShownHelper implements EventHandler<WindowEvent> {
        /**
         * Triggers a <tt>WindowShown</tt> event with the window as sole argument
         */
        public void handle(WindowEvent windowEvent) {
            event(ApplicationEvent.WINDOW_SHOWN, Arrays.asList(windowEvent.getSource()));
        }
    }

    /**
     * Listener that triggers application events when a window is hidden.
     *
     * @author Andres Almiray
     */
    private class OnWindowHiddenHelper implements EventHandler<WindowEvent> {
        /**
         * Triggers a <tt>WindowHidden</tt> event with the window as sole argument
         */
        public void handle(WindowEvent windowEvent) {
            event(ApplicationEvent.WINDOW_HIDDEN, Arrays.asList(windowEvent.getSource()));
        }
    }

    private void event(@Nonnull ApplicationEvent evt, List<Object> args) {
        try {
            EventRouter eventRouter = getApplication().getEventRouter();
            eventRouter.publish(evt.getName(), args);
        } catch (InstanceNotFoundException infe) {
            if (getApplication().getPhase() != ApplicationPhase.SHUTDOWN) {
                throw infe;
            }
        }
    }
}
