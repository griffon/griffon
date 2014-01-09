/*
 * Copyright 2013-2014 the original author or authors.
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

package org.codehaus.griffon.runtime.core;

import griffon.builder.core.CoreBuilderCustomizer;
import griffon.core.addon.GriffonAddon;
import griffon.core.injection.Module;
import griffon.core.mvc.MVCGroupManager;
import griffon.groovy.GroovyAddon;
import griffon.util.BuilderCustomizer;
import griffon.util.CompositeResourceBundleBuilder;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.codehaus.griffon.runtime.core.mvc.GroovyAwareMVCGroupManager;
import org.codehaus.griffon.runtime.util.GroovyAwareCompositeResourceBundleBuilder;
import org.kordamp.jipsy.ServiceProviderFor;

import javax.inject.Named;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
@Named("groovy")
@ServiceProviderFor(Module.class)
public class GroovyModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        bind(CompositeResourceBundleBuilder.class)
            .to(GroovyAwareCompositeResourceBundleBuilder.class)
            .asSingleton();

        bind(GriffonAddon.class)
            .to(GroovyAddon.class)
            .asSingleton();

        bind(MVCGroupManager.class)
            .to(GroovyAwareMVCGroupManager.class)
            .asSingleton();

        bind(BuilderCustomizer.class)
            .to(CoreBuilderCustomizer.class)
            .asSingleton();
    }
}
