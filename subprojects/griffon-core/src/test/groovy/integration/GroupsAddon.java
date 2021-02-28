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
package integration;

import griffon.annotations.core.Nonnull;
import griffon.core.GriffonApplication;
import griffon.util.CollectionUtils;
import org.codehaus.griffon.runtime.core.addon.AbstractGriffonAddon;

import javax.inject.Named;
import java.util.Map;

@Named("groups")
public class GroupsAddon extends AbstractGriffonAddon implements Invokable {
    private boolean invoked;

    @Override
    public void init(@Nonnull GriffonApplication application) {
        invoked = true;
    }

    @Override
    public boolean isInvoked() {
        return invoked;
    }

    @Nonnull
    @Override
    public Map<String, Map<String, Object>> getMvcGroups() {
        return CollectionUtils.<String, Map<String, Object>>map()
            .e("root", CollectionUtils.<String, Object>map()
                .e("model", "integration.RootModel")
                .e("view", "integration.RootView")
                .e("controller", "integration.RootController"))
            .e("child", CollectionUtils.<String, Object>map()
                .e("model", "integration.ChildModel")
                .e("view", "integration.ChildView")
                .e("controller", "integration.ChildController"))
            .e("args", CollectionUtils.<String, Object>map()
                .e("model", "integration.ArgsModel")
                .e("view", "integration.ArgsView")
                .e("controller", "integration.ArgsController"));
    }
}
