package org.codehaus.griffon.runtime.swing;

import griffon.core.CallableWithArgs;
import griffon.core.GriffonApplication;
import griffon.core.view.WindowDisplayHandler;
import griffon.exceptions.InstanceNotFoundException;
import griffon.swing.SwingWindowDisplayHandler;
import org.codehaus.griffon.runtime.core.view.ConfigurableWindowDisplayHandler;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.JInternalFrame;
import java.awt.Window;
import java.util.Map;

import static griffon.util.AnnotationUtils.named;
import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class ConfigurableSwingWindowDisplayHandler extends ConfigurableWindowDisplayHandler<Window> implements SwingWindowDisplayHandler {
    @Inject
    public ConfigurableSwingWindowDisplayHandler(@Nonnull GriffonApplication application, @Nonnull @Named("defaultWindowDisplayHandler") SwingWindowDisplayHandler delegateWindowsDisplayHandler) {
        super(application, delegateWindowsDisplayHandler);
    }

    public void show(@Nonnull String name, @Nonnull JInternalFrame window) {
        requireNonBlank(name, ERROR_NAME_BLANK);
        requireNonNull(window, ERROR_WINDOW_NULL);

        Map<String, Object> options = windowBlock(name);
        if (!options.isEmpty()) {
            Object handler = options.get("show");
            if (canBeRun(handler)) {
                run((CallableWithArgs<?>) handler, window);
                return;
            } else if (options.get("handler") instanceof SwingWindowDisplayHandler) {
                ((SwingWindowDisplayHandler) options.get("handler")).show(name, window);
                return;
            }
        }

        SwingWindowDisplayHandler handler = resolveSwingWindowDisplayHandler(name);
        if (handler != null) {
            handler.show(name, window);
            return;
        }

        options = windowManagerBlock();
        if (!options.isEmpty()) {
            Object defaultShow = options.get("defaultShow");
            if (canBeRun(defaultShow)) {
                run((CallableWithArgs<?>) defaultShow, window);
                return;
            }
        }

        fetchDefaultWindowDisplayHandler().show(name, window);
    }

    public void hide(@Nonnull String name, @Nonnull JInternalFrame window) {
        requireNonBlank(name, ERROR_NAME_BLANK);
        requireNonNull(window, ERROR_WINDOW_NULL);

        Map<String, Object> options = windowBlock(name);
        if (!options.isEmpty()) {
            Object handler = options.get("hide");
            if (canBeRun(handler)) {
                run((CallableWithArgs) handler, window);
                return;
            } else if (options.get("handler") instanceof SwingWindowDisplayHandler) {
                ((SwingWindowDisplayHandler) options.get("handler")).hide(name, window);
                return;
            }
        }

        SwingWindowDisplayHandler handler = resolveSwingWindowDisplayHandler(name);
        if (handler != null) {
            handler.hide(name, window);
            return;
        }

        options = windowManagerBlock();
        if (!options.isEmpty()) {
            Object defaultHide = options.get("defaultHide");
            if (canBeRun(defaultHide)) {
                run((CallableWithArgs<?>) defaultHide, window);
                return;
            }
        }
        fetchDefaultWindowDisplayHandler().hide(name, window);
    }

    protected void run(@Nonnull CallableWithArgs<?> handler, @Nonnull JInternalFrame window) {
        handler.call(window);
    }

    @Nonnull
    protected SwingWindowDisplayHandler fetchDefaultWindowDisplayHandler() {
        Object handler = windowManagerBlock().get("defaultHandler");
        return (SwingWindowDisplayHandler) (handler instanceof SwingWindowDisplayHandler ? handler : getDelegateWindowsDisplayHandler());
    }

    @Override
    protected boolean handleShowByInjectedHandler(@Nonnull String name, @Nonnull Window window) {
        try {
            SwingWindowDisplayHandler handler = getApplication().getInjector()
                .getInstance(SwingWindowDisplayHandler.class, named(name));
            handler.show(name, window);
            return true;
        } catch (InstanceNotFoundException infe) {
            return super.handleShowByInjectedHandler(name, window);
        }
    }

    @Override
    protected boolean handleHideByInjectedHandler(@Nonnull String name, @Nonnull Window window) {
        try {
            SwingWindowDisplayHandler handler = getApplication().getInjector()
                .getInstance(SwingWindowDisplayHandler.class, named(name));
            handler.hide(name, window);
            return true;
        } catch (InstanceNotFoundException infe) {
            return super.handleHideByInjectedHandler(name, window);
        }
    }

    protected SwingWindowDisplayHandler resolveSwingWindowDisplayHandler(@Nonnull String name) {
        try {
            return getApplication().getInjector()
                .getInstance(SwingWindowDisplayHandler.class, named(name));
        } catch (InstanceNotFoundException infe1) {
            try {
                WindowDisplayHandler handler = getApplication().getInjector()
                    .getInstance(WindowDisplayHandler.class, named(name));
                if (handler instanceof SwingWindowDisplayHandler) {
                    return ((SwingWindowDisplayHandler) handler);
                }
            } catch (InstanceNotFoundException infe2) {
                // ignore
            }
        }

        return null;
    }
}
