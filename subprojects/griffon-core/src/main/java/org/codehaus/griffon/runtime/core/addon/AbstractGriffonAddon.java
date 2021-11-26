/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2021 the original author or authors.
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
import griffon.core.artifact.GriffonController;
import griffon.core.artifact.GriffonModel;
import griffon.core.artifact.GriffonView;
import griffon.core.mvc.MVCGroup;
import griffon.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static griffon.util.AnnotationUtils.nameFor;
import static java.util.Objects.requireNonNull;

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

    @Nonnull
    public static Map<String, Map<String, Object>> mvcgroup(@Nonnull Class<? extends MVCGroup> g,
                                                            @Nonnull Class<? extends GriffonModel> m,
                                                            @Nonnull Class<? extends GriffonView> v,
                                                            @Nonnull Class<? extends GriffonController> c) {
        requireNonNull(g, "Argument 'g' must not be null");
        requireNonNull(v, "Argument 'm' must not be null");
        requireNonNull(c, "Argument 'v' must not be null");
        requireNonNull(g, "Argument 'c' must not be null");

        return CollectionUtils.<String, Map<String, Object>>map()
            .e(nameFor(g, true), CollectionUtils.<String, Object>map()
                .e("model", m.getName())
                .e("view", v.getName())
                .e("controller", c.getName())
            );
    }
}
