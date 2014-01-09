package org.codehaus.griffon.runtime.lanterna;

import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.listener.WindowAdapter;
import griffon.core.ApplicationEvent;
import griffon.core.GriffonApplication;
import griffon.core.env.ApplicationPhase;
import griffon.lanterna.LanternaWindowDisplayHandler;
import griffon.lanterna.LanternaWindowManager;
import org.codehaus.griffon.runtime.core.view.AbstractWindowManager;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class DefaultLanternaWindowManager extends AbstractWindowManager<Window> implements LanternaWindowManager {
    private final WindowHelper windowHelper = new WindowHelper();

    @Inject
    @Nonnull
    public DefaultLanternaWindowManager(@Nonnull GriffonApplication application, @Nonnull @Named("windowDisplayHandler") LanternaWindowDisplayHandler windowDisplayHandler) {
        super(application, windowDisplayHandler);
        requireNonNull(application.getEventRouter(), "Argument 'application.eventRouter' cannot be null");
    }

    @Override
    protected void doAttach(@Nonnull Window window) {
        requireNonNull(window, ERROR_WINDOW_NULL);
        window.addWindowListener(windowHelper);
    }

    @Override
    protected void doDetach(@Nonnull Window window) {
        requireNonNull(window, ERROR_WINDOW_NULL);
    }

    @Override
    protected boolean isWindowVisible(@Nonnull Window window) {
        requireNonNull(window, ERROR_WINDOW_NULL);
        return true;
    }

    public void handleClose(@Nonnull Window widget) {
        if (getApplication().getPhase() == ApplicationPhase.SHUTDOWN) {
            return;
        }

        int visibleWindows = getWindows().size();
        if (visibleWindows <= 1 && isAutoShutdown()) {
            if (!getApplication().shutdown())
                show(widget);
        }
    }

    /**
     * WindowAdapter that optionally invokes hide() when the window is about to be closed.
     *
     * @author Andres Almiray
     */
    private class WindowHelper extends WindowAdapter {
        @Override
        public void onWindowClosed(@Nonnull Window window) {
            super.onWindowClosed(window);
            getApplication().getEventRouter().publish(ApplicationEvent.WINDOW_HIDDEN.getName(), asList(window));
            handleClose(window);
        }

        @Override
        public void onWindowShown(Window window) {
            super.onWindowShown(window);
            getApplication().getEventRouter().publish(ApplicationEvent.WINDOW_SHOWN.getName(), asList(window));
        }
    }
}
