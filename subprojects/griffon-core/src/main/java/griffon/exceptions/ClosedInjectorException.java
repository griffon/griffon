package griffon.exceptions;

import griffon.core.injection.Injector;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class ClosedInjectorException extends GriffonException {
    private static final long serialVersionUID = -8727775758022695276L;

    public ClosedInjectorException(Injector<?> injector) {
        super("Injector is closed! " + injector);
    }
}
