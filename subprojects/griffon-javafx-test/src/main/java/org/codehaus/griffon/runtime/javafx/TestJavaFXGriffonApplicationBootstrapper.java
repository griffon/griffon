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
package org.codehaus.griffon.runtime.javafx;

import griffon.core.GriffonApplication;
import griffon.core.injection.Module;
import griffon.core.view.WindowManager;
import org.codehaus.griffon.runtime.core.TestApplicationBootstrapper;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Andres Almiray
 * @since 2.3.0
 */
public class TestJavaFXGriffonApplicationBootstrapper extends TestApplicationBootstrapper {
    public TestJavaFXGriffonApplicationBootstrapper(@Nonnull GriffonApplication application) {
        super(application);
    }

    @Nonnull
    @Override
    protected List<Module> loadModules() {
        List<Module> modules = super.loadModules();
        modules.add(new AbstractModule() {
            @Override
            protected void doConfigure() {
                bind(WindowManager.class)
                    .toProvider(new TestJavaFXWindowManagerProvider())
                    .asSingleton();
            }
        });
        return modules;
    }
}