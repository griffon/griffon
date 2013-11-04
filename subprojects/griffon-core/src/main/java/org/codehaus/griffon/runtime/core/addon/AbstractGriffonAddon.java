/*
 * Copyright 2009-2013 the original author or authors.
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

package org.codehaus.griffon.runtime.core.addon;

import griffon.core.GriffonApplication;
import griffon.core.addon.GriffonAddon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collections;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * @author Andres Almiray
 * @since 0.9.2
 */
public class AbstractGriffonAddon implements GriffonAddon {
    private final Logger log;
    private final GriffonApplication application;

    @Inject
    public AbstractGriffonAddon(@Nonnull GriffonApplication application) {
        this.application = requireNonNull(application, "Argument 'application' cannot be null");
        log = LoggerFactory.getLogger("griffon.addon." + getClass().getSimpleName());
    }

    public GriffonApplication getApplication() {
        return application;
    }

    @Nonnull
    @Override
    public Logger getLog() {
        return log;
    }

    @Override
    public void addonInit(@Nonnull GriffonApplication app) {
        // empty
    }

    @Override
    public void addonPostInit(@Nonnull GriffonApplication app) {
        // empty
    }

    @Nonnull
    @Override
    public Map<String, Map<String, Object>> getMvcGroups() {
        return Collections.emptyMap();
    }

    @Override
    public boolean canShutdown(@Nonnull GriffonApplication application) {
        return true;
    }

    @Override
    public void onShutdown(@Nonnull GriffonApplication application) {
        // empty
    }
}
