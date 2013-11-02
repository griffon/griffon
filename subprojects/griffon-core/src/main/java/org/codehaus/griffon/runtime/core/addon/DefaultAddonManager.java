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

import griffon.core.ApplicationConfiguration;
import griffon.core.addon.GriffonAddonDescriptor;

import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 * @author Andres Almiray
 * @since 0.9.2
 */
public class DefaultAddonManager extends AbstractAddonManager {
    private final ApplicationConfiguration applicationConfiguration;

    @Inject
    public DefaultAddonManager(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    protected ApplicationConfiguration getApplicationConfiguration() {
        return applicationConfiguration;
    }

    protected void doInitialize() {
        // TODO read from applicationConfiguration
    }

    protected void doRegisterAddon(@Nonnull GriffonAddonDescriptor addonDescriptor) {
        getAddonsInternal().put(addonDescriptor.getPluginName(), addonDescriptor);
    }
}
