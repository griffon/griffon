/*
 * SPDX-License-Identifier: Apache-2.0
 *
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
package griffon.lanterna;

import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import org.codehaus.griffon.runtime.core.AbstractGriffonApplication;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class LanternaGriffonApplication extends AbstractGriffonApplication {
    private GUIScreen screen;

    public LanternaGriffonApplication() {
        this(EMPTY_ARGS);
    }

    public LanternaGriffonApplication(@Nonnull String[] args) {
        super(args);
    }

    public GUIScreen getGUIScreen() {
        return getInjector().getInstance(GUIScreen.class);
    }

    @Override
    public void initialize() {
        this.screen = getGUIScreen();
        this.screen.getScreen().startScreen();
        super.initialize();
    }

    @Override
    public boolean shutdown() {
        if (super.shutdown()) {
            exit();
        }
        return false;
    }

    public void exit() {
        this.screen.getScreen().stopScreen();
        System.exit(0);
    }

    @Nonnull
    @Override
    public Object createApplicationContainer(@Nonnull Map<String, Object> attributes) {
        String title = (String) attributes.remove("title");
        if (title == null) {
            title = getConfiguration().getAsString("application.title");
        }
        return new Window(title);
    }

    public static void main(String[] args) throws Exception {
        run(LanternaGriffonApplication.class, args);
    }
}
