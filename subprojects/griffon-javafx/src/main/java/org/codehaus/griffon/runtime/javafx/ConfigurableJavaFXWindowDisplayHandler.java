package org.codehaus.griffon.runtime.javafx;

import griffon.core.ApplicationConfiguration;
import griffon.javafx.JavaFXWindowDisplayHandler;
import javafx.stage.Window;
import org.codehaus.griffon.runtime.core.view.ConfigurableWindowDisplayHandler;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class ConfigurableJavaFXWindowDisplayHandler extends ConfigurableWindowDisplayHandler<Window> implements JavaFXWindowDisplayHandler {
    @Inject
    public ConfigurableJavaFXWindowDisplayHandler(@Nonnull ApplicationConfiguration applicationConfiguration, @Nonnull @Named("defaultWindowDisplayHandler") JavaFXWindowDisplayHandler delegateWindowsDisplayHandler) {
        super(applicationConfiguration, delegateWindowsDisplayHandler);
    }

    @Nonnull
    protected JavaFXWindowDisplayHandler fetchDefaultWindowDisplayHandler() {
        Object handler = windowManagerBlock().get("defaultHandler");
        return (JavaFXWindowDisplayHandler) (handler instanceof JavaFXWindowDisplayHandler ? handler : getDelegateWindowsDisplayHandler());
    }
}
