/*
 * Copyright 2008-2016 the original author or authors.
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
package org.codehaus.griffon.runtime.pivot;

import griffon.core.GriffonApplication;
import griffon.core.injection.Module;
import org.apache.pivot.wtk.Display;
import org.codehaus.griffon.runtime.core.DefaultApplicationBootstrapper;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class PivotApplicationBootstrapper extends DefaultApplicationBootstrapper {
    private final Display display;

    public PivotApplicationBootstrapper(@Nonnull GriffonApplication application, @Nonnull Display display) {
        super(application);
        this.display = display;
    }

    @Override
    protected void collectModuleBindings(@Nonnull Collection<Module> modules) {
        modules.add(new AbstractModule() {
            @Override
            protected void doConfigure() {
                bind(Display.class)
                    .toInstance(display);
            }
        });
        super.collectModuleBindings(modules);
    }
}
