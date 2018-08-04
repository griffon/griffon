package griffon.core.properties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class PropertyChangeEvent<T> {
    private final PropertySource source;
    private final String propertyName;
    private final T oldValue;
    private final T newValue;

    public PropertyChangeEvent(@Nonnull PropertySource source, @Nonnull String propertyName, @Nullable T oldValue, @Nullable T newValue) {
        this.source = requireNonNull(source, "Argument 'source' must not be null");
        this.propertyName = requireNonBlank(propertyName, "Argument 'propertyName' must not be blank");
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Nonnull
    public PropertySource getSource() {
        return source;
    }

    @Nonnull
    public String getPropertyName() {
        return propertyName;
    }

    @Nullable
    public T getOldValue() {
        return oldValue;
    }

    @Nullable
    public T getNewValue() {
        return newValue;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getName());
        sb.append("[propertyName=").append(getPropertyName());
        sb.append("; oldValue=").append(getOldValue());
        sb.append("; newValue=").append(getNewValue());
        sb.append("; source=").append(getSource());
        return sb.append("]").toString();
    }
}
