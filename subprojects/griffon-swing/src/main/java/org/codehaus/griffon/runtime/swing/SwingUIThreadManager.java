/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.griffon.runtime.swing;

import org.codehaus.griffon.runtime.core.threading.AbstractUIThreadManager;

import javax.annotation.Nonnull;
import javax.swing.SwingUtilities;
import java.lang.reflect.InvocationTargetException;

import static java.util.Objects.requireNonNull;

/**
 * Executes code using SwingUtilities.
 *
 * @author Andres Almiray
 */
public class SwingUIThreadManager extends AbstractUIThreadManager {
    @Override
    public boolean isUIThread() {
        return SwingUtilities.isEventDispatchThread();
    }

    @Override
    public void executeInsideUIAsync(@Nonnull Runnable runnable) {
        requireNonNull(runnable, ERROR_RUNNABLE_NULL);
        SwingUtilities.invokeLater(runnable);
    }

    @Override
    public void executeInsideUISync(@Nonnull Runnable runnable) {
        requireNonNull(runnable, ERROR_RUNNABLE_NULL);
        if (isUIThread()) {
            runnable.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(runnable);
            } catch (InterruptedException ie) {
                // ignore
            } catch (InvocationTargetException ite) {
                Throwable t = ite.getCause();
                if (t instanceof RuntimeException) {
                    throw (RuntimeException) t;
                }
                throw new RuntimeException(t);
            }
        }
    }
}
