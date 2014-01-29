/*
 * Copyright 2008-2014 the original author or authors.
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

package org.codehaus.griffon.runtime.core.view;

import griffon.core.GriffonApplication;
import griffon.core.view.WindowManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public final class NoopWindowManager implements WindowManager<Object> {
    private final GriffonApplication application;

    @Inject
    public NoopWindowManager(@Nonnull GriffonApplication application) {
        this.application = requireNonNull(application, "Argument 'application' cannot be null");
    }

    @Nullable
    @Override
    public Object findWindow(@Nonnull String name) {
        return null;
    }

    @Nullable
    @Override
    public Object getAt(int index) {
        return null;
    }

    @Nullable
    @Override
    public Object getStartingWindow() {
        return null;
    }

    @Nonnull
    @Override
    public Collection<Object> getWindows() {
        return Collections.emptyList();
    }

    @Override
    public void attach(@Nonnull String name, @Nonnull Object window) {
    }

    @Override
    public void detach(@Nonnull String name) {
    }

    @Override
    public void show(@Nonnull Object window) {
    }

    @Override
    public void show(@Nonnull String name) {
    }

    @Override
    public void hide(@Nonnull Object window) {
    }

    @Override
    public void hide(@Nonnull String name) {
    }

    @Override
    public boolean canShutdown(@Nonnull GriffonApplication app) {
        return true;
    }

    @Override
    public void onShutdown(@Nonnull GriffonApplication app) {
    }

    @Override
    public int countVisibleWindows() {
        return 0;
    }

    @Override
    public boolean isAutoShutdown() {
        return application.getConfiguration().getAsBoolean("application.autoShutdown", true);
    }
}
