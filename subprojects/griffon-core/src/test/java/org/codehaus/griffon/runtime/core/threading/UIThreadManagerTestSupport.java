package org.codehaus.griffon.runtime.core.threading;

import javax.annotation.Nonnull;

public class UIThreadManagerTestSupport extends AbstractUIThreadManager {
    @Override
    public boolean isUIThread() {
        return false;
    }

    @Override
    public void runInsideUIAsync(@Nonnull Runnable runnable) {
        runnable.run();
    }

    @Override
    public void runInsideUISync(@Nonnull Runnable runnable) {
        runnable.run();
    }
}
