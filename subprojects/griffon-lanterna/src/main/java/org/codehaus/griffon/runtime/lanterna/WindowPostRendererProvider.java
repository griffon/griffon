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
package org.codehaus.griffon.runtime.lanterna;

import com.googlecode.lanterna.graphics.ThemedTextGraphics;
import com.googlecode.lanterna.gui2.TextGUI;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.WindowPostRenderer;

import javax.inject.Provider;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class WindowPostRendererProvider implements Provider<WindowPostRenderer> {
    @Override
    public WindowPostRenderer get() {
        return new WindowPostRenderer() {
            @Override
            public void postRender(ThemedTextGraphics textGraphics, TextGUI textGUI, Window window) {
                // NOOP
            }
        };
    }
}
