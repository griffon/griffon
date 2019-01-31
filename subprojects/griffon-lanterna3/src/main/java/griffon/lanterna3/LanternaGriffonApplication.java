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
package griffon.lanterna3;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.screen.Screen;
import griffon.annotations.core.Nonnull;
import org.codehaus.griffon.runtime.core.AbstractGriffonApplication;

import java.io.IOException;
import java.util.Map;

/**
 * @author Andres Almiray
 * @since 3.0.0
 */
public class LanternaGriffonApplication extends AbstractGriffonApplication {
    private Screen screen;

    public LanternaGriffonApplication() {
        this(EMPTY_ARGS);
    }

    public LanternaGriffonApplication(@Nonnull String[] args) {
        super(args);
    }

    public Screen getScreen() {
        return getInjector().getInstance(Screen.class);
    }

    @Override
    public void initialize() {
        this.screen = getScreen();
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
        try {
            this.screen.stopScreen();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        System.exit(0);
    }

    @Nonnull
    @Override
    public Object createApplicationContainer(@Nonnull Map<String, Object> attributes) {
        CharSequence title = (CharSequence) attributes.remove("title");
        if (title == null) {
            title = getConfiguration().getAsString("application.title");
        }
        return new BasicWindow(String.valueOf(title));
    }

    public static void main(String[] args) throws Exception {
        run(LanternaGriffonApplication.class, args);
    }
}
