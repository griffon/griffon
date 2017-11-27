package griffon.javafx.beans.binding;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Binding;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.13.0
 */
public class BindingDecorator<T> implements Binding<T> {
    private final Binding<T> delegate;

    public BindingDecorator(@Nonnull Binding<T> delegate) {
        this.delegate = requireNonNull(delegate, "Argument 'delegate' must not be null");
    }

    @Nonnull
    protected final Binding<T> getDelegate() {
        return delegate;
    }

    @Override
    public boolean isValid() {
        return getDelegate().isValid();
    }

    @Override
    public void invalidate() {
        getDelegate().invalidate();
    }

    @Override
    public ObservableList<?> getDependencies() {
        return getDelegate().getDependencies();
    }

    @Override
    public void dispose() {
        getDelegate().dispose();
    }

    @Override
    public void addListener(ChangeListener<? super T> listener) {
        getDelegate().addListener(listener);
    }

    @Override
    public void removeListener(ChangeListener<? super T> listener) {
        getDelegate().removeListener(listener);
    }

    @Override
    public T getValue() {
        return getDelegate().getValue();
    }

    @Override
    public void addListener(InvalidationListener listener) {
        getDelegate().addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        getDelegate().removeListener(listener);
    }
}
