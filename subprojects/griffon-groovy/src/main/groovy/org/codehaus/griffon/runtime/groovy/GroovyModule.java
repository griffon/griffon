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
package org.codehaus.griffon.runtime.groovy;

import griffon.builder.core.CoreBuilderCustomizer;
import griffon.core.addon.GriffonAddon;
import griffon.core.bundles.ResourceBundleLoader;
import griffon.core.injection.Module;
import griffon.core.mvc.MVCGroupFactory;
import griffon.core.mvc.MVCGroupManager;
import griffon.core.util.groovy.BuilderCustomizer;
import griffon.core.util.groovy.ConfigReader;
import org.codehaus.griffon.runtime.core.i18n.MessageSourceDecoratorFactory;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.codehaus.griffon.runtime.core.resources.ResourceResolverDecoratorFactory;
import org.codehaus.griffon.runtime.groovy.bundles.GroovyScriptResourceBundleLoader;
import org.codehaus.griffon.runtime.groovy.i18n.GroovyAwareMessageSourceDecoratorFactory;
import org.codehaus.griffon.runtime.groovy.mvc.GroovyAwareMVCGroupFactory;
import org.codehaus.griffon.runtime.groovy.mvc.GroovyAwareMVCGroupManager;
import org.codehaus.griffon.runtime.groovy.resources.GroovyAwareResourceResolverDecoratorFactory;
import org.kordamp.jipsy.annotations.ServiceProviderFor;

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
        // tag::bindings[]
        bind(ConfigReader.class)
            .toProvider(ConfigReader.Provider.class)
            .asSingleton();

        bind(ResourceBundleLoader.class)
            .to(GroovyScriptResourceBundleLoader.class)
            .asSingleton();

        bind(GriffonAddon.class)
            .to(GroovyAddon.class)
            .asSingleton();

        bind(MVCGroupFactory.class)
            .to(GroovyAwareMVCGroupFactory.class)
            .asSingleton();

        bind(MVCGroupManager.class)
            .to(GroovyAwareMVCGroupManager.class)
            .asSingleton();

        bind(BuilderCustomizer.class)
            .to(CoreBuilderCustomizer.class)
            .asSingleton();

        bind(ResourceResolverDecoratorFactory.class)
            .to(GroovyAwareResourceResolverDecoratorFactory.class);

        bind(MessageSourceDecoratorFactory.class)
            .to(GroovyAwareMessageSourceDecoratorFactory.class);
        // end::bindings[]
    }
}
