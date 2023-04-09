/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2023 the original author or authors.
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
package integration;

import griffon.annotations.core.Nonnull;
import griffon.util.CollectionUtils;
import org.codehaus.griffon.runtime.core.bundles.AbstractMapResourceBundle;

import java.util.Map;

import static java.util.Collections.singletonList;

public class Config extends AbstractMapResourceBundle {
    @Override
    protected void initialize(@Nonnull Map<String, Object> entries) {
        CollectionUtils.map(entries)
            .e("application", CollectionUtils.map()
                .e("title", "integration")
                .e("startupGroups", singletonList("integration"))
                .e("autoShutdown", true)
            )
            .e("mvcGroups", CollectionUtils.map()
                .e("integration", CollectionUtils.map()
                    .e("model", "integration.IntegrationModel")
                    .e("view", "integration.IntegrationView")
                    .e("controller", "integration.IntegrationController")
                    .e("config", CollectionUtils.map()
                        .e("color", "#0000FF"))
                )
            )
            .e("keys", CollectionUtils.map()
                .e("key1", "value1"));
    }
}