/*
 * Copyright 2008-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package griffon.swing;

import griffon.core.UIThreadManager;
import griffon.util.UIThreadHandler;

import java.awt.*;

/**
 * Simple implementation of {@code GriffonApplication} that runs in applet mode.
 *
 * @author Danno Ferrin
 * @author Andres Almiray
 * @since 0.1
 */
public class SwingApplet extends AbstractGriffonApplet implements SwingGriffonApplication {
    private boolean appletContainerDispensed = false;
    private final WindowManager windowManager;
    private WindowDisplayHandler windowDisplayHandler;
    private final WindowDisplayHandler defaultWindowDisplayHandler = new ConfigurableWindowDisplayHandler();

    public SwingApplet() {
        this(EMPTY_ARGS);
    }

    public SwingApplet(String[] args) {
        super(args);

        windowManager = new WindowManager(this);
        UIThreadManager.getInstance().setUIThreadHandler(getUIThreadHandler());
        addShutdownHandler(windowManager);
    }

    public WindowManager getWindowManager() {
        return windowManager;
    }

    public WindowDisplayHandler getWindowDisplayHandler() {
        return windowDisplayHandler;
    }

    public void setWindowDisplayHandler(WindowDisplayHandler windowDisplayHandler) {
        this.windowDisplayHandler = windowDisplayHandler;
    }

    protected UIThreadHandler getUIThreadHandler() {
        return new SwingUIThreadHandler();
    }

    public final WindowDisplayHandler resolveWindowDisplayHandler() {
        return windowDisplayHandler != null ? windowDisplayHandler : defaultWindowDisplayHandler;
    }

    public Object createApplicationContainer() {
        if (appletContainerDispensed) {
            Window window = SwingUtils.createApplicationFrame(this);
            windowManager.attach(window);
            return window;
        } else {
            appletContainerDispensed = true;
            return this;
        }
    }
}