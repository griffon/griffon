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

import com.googlecode.lanterna.gui2.Component;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.TextGUIThreadFactory;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.WindowManager;
import com.googlecode.lanterna.gui2.WindowPostRenderer;
import com.googlecode.lanterna.screen.Screen;
import griffon.annotations.core.Nonnull;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class WindowBasedTextGUIProvider implements Provider<WindowBasedTextGUI> {
    private final TextGUIThreadFactory textGUIThreadFactory;
    private final Screen screen;
    private final WindowManager windowManager;
    private final WindowPostRenderer windowPostRenderer;
    private final Component background;

    @Inject
    public WindowBasedTextGUIProvider(@Nonnull TextGUIThreadFactory textGUIThreadFactory,
                                      @Nonnull Screen screen,
                                      @Nonnull WindowManager windowManager,
                                      @Nonnull WindowPostRenderer windowPostRenderer,
                                      @Nonnull @Named("background") Component background) {
        this.textGUIThreadFactory = textGUIThreadFactory;
        this.screen = screen;
        this.windowManager = windowManager;
        this.windowPostRenderer = windowPostRenderer;
        this.background = background;
    }

    @Override
    public WindowBasedTextGUI get() {
        return new MultiWindowTextGUI(textGUIThreadFactory,
            screen,
            windowManager,
            windowPostRenderer,
            background);
    }
}
