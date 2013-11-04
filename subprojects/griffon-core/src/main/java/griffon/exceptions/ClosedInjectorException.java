package griffon.exceptions;

import griffon.core.injection.Injector;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class ClosedInjectorException extends GriffonException {
    public ClosedInjectorException(Injector injector) {
        super("Injector is closed! " + injector);
    }
}
