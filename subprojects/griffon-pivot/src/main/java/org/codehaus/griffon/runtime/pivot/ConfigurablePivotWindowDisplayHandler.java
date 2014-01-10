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

package org.codehaus.griffon.runtime.pivot;

import griffon.core.ApplicationConfiguration;
import griffon.pivot.PivotWindowDisplayHandler;
import org.apache.pivot.wtk.Window;
import org.codehaus.griffon.runtime.core.view.ConfigurableWindowDisplayHandler;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class ConfigurablePivotWindowDisplayHandler extends ConfigurableWindowDisplayHandler<Window> implements PivotWindowDisplayHandler {
    @Inject
    public ConfigurablePivotWindowDisplayHandler(@Nonnull ApplicationConfiguration applicationConfiguration, @Nonnull @Named("defaultWindowDisplayHandler") PivotWindowDisplayHandler delegateWindowsDisplayHandler) {
        super(applicationConfiguration, delegateWindowsDisplayHandler);
    }

    @Nonnull
    protected PivotWindowDisplayHandler fetchDefaultWindowDisplayHandler() {
        Object handler = windowManagerBlock().get("defaultHandler");
        return (PivotWindowDisplayHandler) (handler instanceof PivotWindowDisplayHandler ? handler : getDelegateWindowsDisplayHandler());
    }
}
