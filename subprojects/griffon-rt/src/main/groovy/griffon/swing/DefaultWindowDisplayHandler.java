/*
 * Copyright 2008-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language govnerning permissions and
 * limitations under the License.
 */
package griffon.swing;

import griffon.core.GriffonApplication;

import java.awt.*;

/**
 * Default implementation of {@code WindowDisplayHandler} that simply makes the window
 * visible on show() and disposes it on hide().
 *
 * @author Andres Almiray
 * @since 0.3.1
 */
public class DefaultWindowDisplayHandler implements WindowDisplayHandler {
    public void show(Window window, GriffonApplication application) {
        if (window != null) window.setVisible(true);
    }

    public void hide(Window window, GriffonApplication application) {
        if (window != null) window.dispose();
    }
}