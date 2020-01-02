/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2020 the original author or authors.
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

import griffon.core.LifecycleHandler;
import griffon.core.addon.GriffonAddon;
import griffon.core.controller.ActionHandler;
import griffon.core.env.Lifecycle;
import griffon.core.i18n.MessageSource;
import griffon.core.injection.Module;
import griffon.core.resources.ResourceResolver;
import org.codehaus.griffon.runtime.core.LifecycleHandlerProvider;
import org.codehaus.griffon.runtime.core.i18n.MessageSourceProvider;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.codehaus.griffon.runtime.core.resources.ResourceResolverProvider;
import org.codehaus.griffon.runtime.util.ResourceBundleProvider;
import org.kordamp.jipsy.ServiceProviderFor;

import javax.inject.Named;
import java.util.ResourceBundle;

import static griffon.util.AnnotationUtils.named;

@ServiceProviderFor(Module.class)
@Named("integration")
public class IntegrationModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        bind(ResourceBundle.class)
            .withClassifier(named("applicationResourceBundle"))
            .toProvider(new ResourceBundleProvider("integration.Config"))
            .asSingleton();

        bind(ResourceResolver.class)
            .withClassifier(named("applicationResourceResolver"))
            .toProvider(new ResourceResolverProvider("integration.resources"))
            .asSingleton();

        bind(MessageSource.class)
            .withClassifier(named("applicationMessageSource"))
            .toProvider(new MessageSourceProvider("integration.messages"))
            .asSingleton();

        for (Lifecycle lifecycle : Lifecycle.values()) {
            bind(LifecycleHandler.class)
                .withClassifier(named(lifecycle.getName()))
                .toProvider(new LifecycleHandlerProvider("integration.Integration" + lifecycle.getName()))
                .asSingleton();
        }

        bind(GriffonAddon.class)
            .to(IntegrationAddon.class)
            .asSingleton();

        bind(GriffonAddon.class)
            .to(GroupsAddon.class)
            .asSingleton();

        bind(ActionHandler.class)
            .to(InvokeActionHandler.class)
            .asSingleton();

        bind(ContextualBean.class);
    }
}
