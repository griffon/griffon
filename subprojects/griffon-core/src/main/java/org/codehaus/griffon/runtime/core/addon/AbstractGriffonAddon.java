/*
 * Copyright 2008-2015 the original author or authors.
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
package org.codehaus.griffon.runtime.core.addon;

import griffon.core.GriffonApplication;
import griffon.core.addon.GriffonAddon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class AbstractGriffonAddon implements GriffonAddon {
    private final Logger log;

    @Inject
    public AbstractGriffonAddon() {
        log = LoggerFactory.getLogger("griffon.addon." + getClass().getSimpleName());
    }

    @Nonnull
    @Override
    public Logger getLog() {
        return log;
    }

    @Override
    public void init(@Nonnull GriffonApplication application) {
        // empty
    }

    @Nonnull
    @Override
    public Map<String, Map<String, Object>> getMvcGroups() {
        return Collections.emptyMap();
    }

    @Nonnull
    @Override
    public List<String> getStartupGroups() {
        return Collections.emptyList();
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
