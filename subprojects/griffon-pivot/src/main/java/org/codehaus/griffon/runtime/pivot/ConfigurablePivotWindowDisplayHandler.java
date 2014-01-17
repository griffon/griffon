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

import griffon.core.GriffonApplication;
import griffon.exceptions.InstanceNotFoundException;
import griffon.pivot.PivotWindowDisplayHandler;
import org.apache.pivot.wtk.Window;
import org.codehaus.griffon.runtime.core.view.ConfigurableWindowDisplayHandler;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

import static griffon.util.AnnotationUtils.named;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class ConfigurablePivotWindowDisplayHandler extends ConfigurableWindowDisplayHandler<Window> implements PivotWindowDisplayHandler {
    @Inject
    public ConfigurablePivotWindowDisplayHandler(@Nonnull GriffonApplication application, @Nonnull @Named("defaultWindowDisplayHandler") PivotWindowDisplayHandler delegateWindowsDisplayHandler) {
        super(application, delegateWindowsDisplayHandler);
    }

    @Nonnull
    protected PivotWindowDisplayHandler fetchDefaultWindowDisplayHandler() {
        Object handler = windowManagerBlock().get("defaultHandler");
        return (PivotWindowDisplayHandler) (handler instanceof PivotWindowDisplayHandler ? handler : getDelegateWindowsDisplayHandler());
    }

    @Override
    protected boolean handleShowByInjectedHandler(@Nonnull String name, @Nonnull Window window) {
        try {
            PivotWindowDisplayHandler handler = getApplication().getInjector()
                .getInstance(PivotWindowDisplayHandler.class, named(name));
            handler.show(name, window);
            return true;
        } catch (InstanceNotFoundException infe) {
            return super.handleShowByInjectedHandler(name, window);
        }
    }

    @Override
    protected boolean handleHideByInjectedHandler(@Nonnull String name, @Nonnull Window window) {
        try {
            PivotWindowDisplayHandler handler = getApplication().getInjector()
                .getInstance(PivotWindowDisplayHandler.class, named(name));
            handler.hide(name, window);
            return true;
        } catch (InstanceNotFoundException infe) {
            return super.handleHideByInjectedHandler(name, window);
        }
    }
}
