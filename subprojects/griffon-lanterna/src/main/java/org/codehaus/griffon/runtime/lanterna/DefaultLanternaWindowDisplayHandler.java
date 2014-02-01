/*
 * Copyright 2008-2014 the original author or authors.
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

import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import griffon.lanterna.LanternaWindowDisplayHandler;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static griffon.util.GriffonNameUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class DefaultLanternaWindowDisplayHandler implements LanternaWindowDisplayHandler {
    private static final String ERROR_NAME_BLANK = "Argument 'name' cannot be null";
    private static final String ERROR_WINDOW_NULL = "Argument 'window' cannot be null";

    private final GUIScreen screen;

    @Inject
    public DefaultLanternaWindowDisplayHandler(@Nonnull GUIScreen screen) {
        this.screen = requireNonNull(screen, "Argument 'screen' cannot be null");
    }

    public void show(@Nonnull String name, @Nonnull Window window) {
        requireNonBlank(name, ERROR_NAME_BLANK);
        requireNonNull(window, ERROR_WINDOW_NULL);
        screen.showWindow(window, GUIScreen.Position.CENTER);
    }

    public void hide(@Nonnull String name, @Nonnull Window window) {
        requireNonBlank(name, ERROR_NAME_BLANK);
        requireNonNull(window, ERROR_WINDOW_NULL);
        window.close();
    }
}