/*
 * Copyright 2009-2014 the original author or authors.
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

package org.codehaus.griffon.runtime.lanterna;

import com.googlecode.lanterna.gui.Window;
import griffon.core.ApplicationConfiguration;
import griffon.lanterna.LanternaWindowDisplayHandler;
import org.codehaus.griffon.runtime.core.view.ConfigurableWindowDisplayHandler;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class ConfigurableLanternaWindowDisplayHandler extends ConfigurableWindowDisplayHandler<Window> implements LanternaWindowDisplayHandler {
    @Inject
    public ConfigurableLanternaWindowDisplayHandler(@Nonnull ApplicationConfiguration applicationConfiguration, @Nonnull @Named("defaultWindowDisplayHandler") LanternaWindowDisplayHandler delegateWindowsDisplayHandler) {
        super(applicationConfiguration, delegateWindowsDisplayHandler);
    }

    @Nonnull
    protected LanternaWindowDisplayHandler fetchDefaultWindowDisplayHandler() {
        Object handler = windowManagerBlock().get("defaultHandler");
        return (LanternaWindowDisplayHandler) (handler instanceof LanternaWindowDisplayHandler ? handler : getDelegateWindowsDisplayHandler());
    }
}
