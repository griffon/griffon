package org.codehaus.griffon.runtime.swing;

import griffon.core.ApplicationConfiguration;
import griffon.core.CallableWithArgs;
import griffon.swing.SwingWindowDisplayHandler;
import org.codehaus.griffon.runtime.core.view.ConfigurableWindowDisplayHandler;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.JInternalFrame;
import java.awt.Window;
import java.util.Map;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class ConfigurableSwingWindowDisplayHandler extends ConfigurableWindowDisplayHandler<Window> implements SwingWindowDisplayHandler {
    @Inject
    public ConfigurableSwingWindowDisplayHandler(@Nonnull ApplicationConfiguration applicationConfiguration, @Nonnull @Named("defaultWindowDisplayHandler") SwingWindowDisplayHandler delegateWindowsDisplayHandler) {
        super(applicationConfiguration, delegateWindowsDisplayHandler);
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

        options = windowManagerBlock();
        if (!options.isEmpty()) {
            Object handler = options.get("defaultShow");
            if (canBeRun(handler)) {
                run((CallableWithArgs<?>) handler, window);
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

        options = windowManagerBlock();
        if (!options.isEmpty()) {
            Object handler = options.get("defaultHide");
            if (canBeRun(handler)) {
                run((CallableWithArgs<?>) handler, window);
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
}
