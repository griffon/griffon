/*
 * Copyright 2008-2017 the original author or authors.
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
package org.codehaus.griffon.runtime.lanterna;

import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.GUIScreen;
import org.codehaus.griffon.runtime.core.threading.AbstractUIThreadManager;

import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 * Executes code honoring Lanterna's threading model.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public class LanternaUIThreadManager extends AbstractUIThreadManager {
    private final GUIScreen screen;

    @Inject
    public LanternaUIThreadManager(@Nonnull GUIScreen screen) {
        this.screen = screen;
    }

    public boolean isUIThread() {
        return screen.isInEventThread();
    }

    @Override
    public void runInsideUIAsync(@Nonnull Runnable runnable) {
        runInsideUISync(runnable);
    }

    @Override
    public void runInsideUISync(@Nonnull final Runnable runnable) {
        if (isUIThread()) {
            runnable.run();
        } else {
            screen.runInEventThread(new Action() {
                public void doAction() {
                    runnable.run();
                }
            });
        }
    }
}
