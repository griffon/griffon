package griffon.core.properties;

import javax.annotation.Nonnull;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public interface PropertyChangeListener<T> {
    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source
     *            and the property that has changed.
     */
    void propertyChange(@Nonnull PropertyChangeEvent<T> evt);
}
