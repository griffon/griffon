/*
 * Copyright 2016-2018 the original author or authors.
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
package org.example.calculator;

import griffon.core.addon.GriffonAddon;
import griffon.core.injection.Module;
import griffon.inject.DependsOn;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.kordamp.jipsy.annotations.ServiceProviderFor;

import javax.inject.Named;

@DependsOn("javafx")
@Named("calculator")
@ServiceProviderFor(Module.class)
public class CalculatorModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        bind(GriffonAddon.class)
            .to(CalculatorAddon.class)
            .asSingleton();
    }
}
