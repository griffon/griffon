package org.codehaus.griffon.runtime.core.threading;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class DefaultUIThreadManager extends AbstractUIThreadManager {
    @Override
    public boolean isUIThread() {
        return false;
    }

    @Override
    public void runInsideUIAsync(@Nonnull Runnable runnable) {
        requireNonNull(runnable, ERROR_RUNNABLE_NULL);
        runnable.run();
    }

    @Override
    public void runInsideUISync(@Nonnull Runnable runnable) {
        requireNonNull(runnable, ERROR_RUNNABLE_NULL);
        runnable.run();
    }
}
