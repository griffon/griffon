/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2019 the original author or authors.
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
package org.codehaus.griffon.runtime.lanterna3;

import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import griffon.annotations.core.Nonnull;
import org.codehaus.griffon.runtime.core.threading.AbstractUIThreadManager;

import javax.inject.Inject;

/**
 * Executes code honoring Lanterna's threading model.
 *
 * @author Andres Almiray
 * @since 3.0.0
 */
public class LanternaUIThreadManager extends AbstractUIThreadManager {
    private final WindowBasedTextGUI windowBasedTextGUI;

    @Inject
    public LanternaUIThreadManager(@Nonnull WindowBasedTextGUI windowBasedTextGUI) {
        this.windowBasedTextGUI = windowBasedTextGUI;
    }

    public boolean isUIThread() {
        return windowBasedTextGUI.getGUIThread().getThread() == Thread.currentThread();
    }

    @Override
    public void executeInsideUIAsync(@Nonnull Runnable runnable) {
        executeInsideUISync(runnable);
    }

    @Override
    public void executeInsideUISync(@Nonnull final Runnable runnable) {
        if (isUIThread()) {
            runnable.run();
        } else {
            try {
                windowBasedTextGUI.getGUIThread().invokeAndWait(runnable);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
