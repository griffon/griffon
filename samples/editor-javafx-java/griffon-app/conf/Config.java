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

import griffon.annotations.core.Nonnull;
import org.codehaus.griffon.runtime.core.bundles.AbstractMapResourceBundle;

import java.util.Map;

import static griffon.util.CollectionUtils.map;
import static java.util.Collections.singletonList;

public class Config extends AbstractMapResourceBundle {
    @Override
    protected void initialize(@Nonnull Map<String, Object> entries) {
        map(entries)
            .e("application", map()
                .e("title", "editor-javafx-java")
                .e("startupGroups", singletonList("container"))
                .e("autoShutdown", true)
            )
            .e("mvcGroups", map()
                .e("container", map()
                    .e("model", "editor.ContainerModel")
                    .e("view", "editor.ContainerView")
                    .e("controller", "editor.ContainerController")
                )
                .e("editor", map()
                    .e("model", "editor.EditorModel")
                    .e("view", "editor.EditorView")
                    .e("controller", "editor.EditorController")
                )
            );
    }
}