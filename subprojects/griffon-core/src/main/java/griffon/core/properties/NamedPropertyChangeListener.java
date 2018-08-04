package griffon.core.properties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class NamedPropertyChangeListener<T> implements PropertyChangeListener<T> {
    private final String propertyName;
    private final PropertyChangeListener<T> delegate;

    public NamedPropertyChangeListener(@Nonnull String propertyName, @Nonnull PropertyChangeListener<T> delegate) {
        this.propertyName = requireNonBlank(propertyName, "Argument 'propertyName' must not be blank");
        this.delegate = requireNonNull(delegate, "Argument 'delegate' must not be null");
    }

    @Nonnull
    public String getPropertyName() {
        return propertyName;
    }

    @Nonnull
    public PropertyChangeListener<T> getDelegate() {
        return delegate;
    }

    @Override
    public void propertyChange(@Nonnull PropertyChangeEvent<T> evt) {
        delegate.propertyChange(evt);
    }

    public static <T> boolean isWrappedBy(@Nullable PropertyChangeListener<T> listener, @Nullable NamedPropertyChangeListener<T> wrapper) {
        if (listener == null || wrapper == null) {
            return false;
        }

        return wrapper.delegate == listener;
    }
}
