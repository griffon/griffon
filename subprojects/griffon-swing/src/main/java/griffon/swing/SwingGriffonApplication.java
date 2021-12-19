/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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
package griffon.swing;

import griffon.annotations.core.Nonnull;
import griffon.swing.support.SwingUtils;
import org.codehaus.griffon.runtime.core.AbstractGriffonApplication;

import java.awt.*;
import java.util.Map;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class SwingGriffonApplication extends AbstractGriffonApplication {
    public SwingGriffonApplication() {
        this(EMPTY_ARGS);
    }

    public SwingGriffonApplication(@Nonnull String[] args) {
        super(args);
    }

    @Override
    protected void showStartingWindow() {
        super.showStartingWindow();

        // wait for EDT to empty out.... somehow
        final boolean[] empty = {false};
        while (true) {
            getUIThreadManager().runInsideUISync(() -> {
                empty[0] = Toolkit.getDefaultToolkit().getSystemEventQueue().peekEvent() == null;
            });
            if (empty[0]) break;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    @Override
    public boolean shutdown() {
        if (super.shutdown()) {
            exit();
        }
        return false;
    }

    public void exit() {
        System.exit(0);
    }

    @Nonnull
    @Override
    public Object createApplicationContainer(@Nonnull Map<String, Object> attributes) {
        return SwingUtils.createApplicationFrame(this, attributes);
    }

    public static void main(String[] args) throws Exception {
        run(SwingGriffonApplication.class, args);
    }
}
