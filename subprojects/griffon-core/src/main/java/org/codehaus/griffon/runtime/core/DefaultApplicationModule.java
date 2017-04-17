/*
 * Copyright 2008-2017 the original author or authors.
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
package org.codehaus.griffon.runtime.core;

import griffon.core.ApplicationClassLoader;
import griffon.core.ApplicationConfigurer;
import griffon.core.Configuration;
import griffon.core.Context;
import griffon.core.ContextFactory;
import griffon.core.ExceptionHandler;
import griffon.core.ExecutorServiceManager;
import griffon.core.LifecycleHandler;
import griffon.core.PlatformHandler;
import griffon.core.addon.AddonManager;
import griffon.core.artifact.ArtifactHandler;
import griffon.core.artifact.ArtifactManager;
import griffon.core.configuration.ConfigurationManager;
import griffon.core.controller.ActionFactory;
import griffon.core.controller.ActionManager;
import griffon.core.controller.ActionMetadataFactory;
import griffon.core.env.Environment;
import griffon.core.env.Lifecycle;
import griffon.core.env.Metadata;
import griffon.core.env.RunMode;
import griffon.core.event.EventHandler;
import griffon.core.event.EventRouter;
import griffon.core.i18n.MessageSource;
import griffon.core.mvc.MVCGroupConfigurationFactory;
import griffon.core.mvc.MVCGroupFactory;
import griffon.core.mvc.MVCGroupManager;
import griffon.core.resources.ResourceHandler;
import griffon.core.resources.ResourceInjector;
import griffon.core.resources.ResourceResolver;
import griffon.core.threading.UIThreadManager;
import griffon.core.view.WindowManager;
import griffon.util.CompositeResourceBundleBuilder;
import griffon.util.Instantiator;
import griffon.util.PropertiesReader;
import griffon.util.ResourceBundleLoader;
import griffon.util.ResourceBundleReader;
import org.codehaus.griffon.runtime.core.addon.DefaultAddonManager;
import org.codehaus.griffon.runtime.core.artifact.ControllerArtifactHandler;
import org.codehaus.griffon.runtime.core.artifact.DefaultArtifactManager;
import org.codehaus.griffon.runtime.core.artifact.ModelArtifactHandler;
import org.codehaus.griffon.runtime.core.artifact.ServiceArtifactHandler;
import org.codehaus.griffon.runtime.core.artifact.ViewArtifactHandler;
import org.codehaus.griffon.runtime.core.configuration.ConfigurationDecoratorFactory;
import org.codehaus.griffon.runtime.core.configuration.DefaultConfigurationDecoratorFactory;
import org.codehaus.griffon.runtime.core.configuration.DefaultConfigurationManager;
import org.codehaus.griffon.runtime.core.configuration.ResourceBundleConfigurationProvider;
import org.codehaus.griffon.runtime.core.controller.DefaultActionFactory;
import org.codehaus.griffon.runtime.core.controller.DefaultActionManager;
import org.codehaus.griffon.runtime.core.controller.DefaultActionMetadataFactory;
import org.codehaus.griffon.runtime.core.env.EnvironmentProvider;
import org.codehaus.griffon.runtime.core.env.MetadataProvider;
import org.codehaus.griffon.runtime.core.env.RunModeProvider;
import org.codehaus.griffon.runtime.core.event.DefaultEventHandler;
import org.codehaus.griffon.runtime.core.event.DefaultEventRouter;
import org.codehaus.griffon.runtime.core.i18n.DefaultMessageSourceDecoratorFactory;
import org.codehaus.griffon.runtime.core.i18n.MessageSourceDecoratorFactory;
import org.codehaus.griffon.runtime.core.i18n.MessageSourceProvider;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.codehaus.griffon.runtime.core.mvc.DefaultMVCGroupConfigurationFactory;
import org.codehaus.griffon.runtime.core.mvc.DefaultMVCGroupFactory;
import org.codehaus.griffon.runtime.core.mvc.DefaultMVCGroupManager;
import org.codehaus.griffon.runtime.core.resources.DefaultApplicationResourceInjector;
import org.codehaus.griffon.runtime.core.resources.DefaultResourceHandler;
import org.codehaus.griffon.runtime.core.resources.DefaultResourceResolverDecoratorFactory;
import org.codehaus.griffon.runtime.core.resources.ResourceResolverDecoratorFactory;
import org.codehaus.griffon.runtime.core.resources.ResourceResolverProvider;
import org.codehaus.griffon.runtime.core.threading.DefaultExecutorServiceProvider;
import org.codehaus.griffon.runtime.core.threading.DefaultUIThreadManager;
import org.codehaus.griffon.runtime.core.view.NoopWindowManager;
import org.codehaus.griffon.runtime.util.ClassResourceBundleLoader;
import org.codehaus.griffon.runtime.util.DefaultCompositeResourceBundleBuilder;
import org.codehaus.griffon.runtime.util.DefaultInstantiator;
import org.codehaus.griffon.runtime.util.PropertiesResourceBundleLoader;
import org.codehaus.griffon.runtime.util.ResourceBundleProvider;
import org.codehaus.griffon.runtime.util.XmlResourceBundleLoader;

import javax.inject.Named;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;

import static griffon.util.AnnotationUtils.named;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
@Named("core")
public class DefaultApplicationModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        // tag::bindings[]
        bind(ApplicationClassLoader.class)
            .to(DefaultApplicationClassLoader.class)
            .asSingleton();

        bind(Metadata.class)
            .toProvider(MetadataProvider.class)
            .asSingleton();

        bind(RunMode.class)
            .toProvider(RunModeProvider.class)
            .asSingleton();

        bind(Environment.class)
            .toProvider(EnvironmentProvider.class)
            .asSingleton();

        bind(ContextFactory.class)
            .to(DefaultContextFactory.class)
            .asSingleton();

        bind(Instantiator.class)
            .to(DefaultInstantiator.class)
            .asSingleton();

        bind(PropertiesReader.class)
            .toProvider(PropertiesReader.Provider.class)
            .asSingleton();

        bind(ResourceBundleReader.class)
            .toProvider(ResourceBundleReader.Provider.class)
            .asSingleton();

        bind(Context.class)
            .withClassifier(named("applicationContext"))
            .toProvider(DefaultContextProvider.class)
            .asSingleton();

        bind(ApplicationConfigurer.class)
            .to(DefaultApplicationConfigurer.class)
            .asSingleton();

        bind(ResourceHandler.class)
            .to(DefaultResourceHandler.class)
            .asSingleton();

        bind(ResourceBundleLoader.class)
            .to(ClassResourceBundleLoader.class)
            .asSingleton();

        bind(ResourceBundleLoader.class)
            .to(PropertiesResourceBundleLoader.class)
            .asSingleton();

        bind(ResourceBundleLoader.class)
            .to(XmlResourceBundleLoader.class)
            .asSingleton();

        bind(CompositeResourceBundleBuilder.class)
            .to(DefaultCompositeResourceBundleBuilder.class)
            .asSingleton();

        bind(ResourceBundle.class)
            .withClassifier(named("applicationResourceBundle"))
            .toProvider(new ResourceBundleProvider("Config"))
            .asSingleton();

        bind(ConfigurationManager.class)
            .to(DefaultConfigurationManager.class)
            .asSingleton();

        bind(ConfigurationDecoratorFactory.class)
            .to(DefaultConfigurationDecoratorFactory.class);

        bind(Configuration.class)
            .toProvider(ResourceBundleConfigurationProvider.class)
            .asSingleton();

        bind(ExecutorServiceManager.class)
            .to(DefaultExecutorServiceManager.class)
            .asSingleton();

        bind(EventRouter.class)
            .withClassifier(named("applicationEventRouter"))
            .to(DefaultEventRouter.class)
            .asSingleton();

        bind(EventRouter.class)
            .to(DefaultEventRouter.class);

        bind(ResourceResolverDecoratorFactory.class)
            .to(DefaultResourceResolverDecoratorFactory.class);

        bind(MessageSourceDecoratorFactory.class)
            .to(DefaultMessageSourceDecoratorFactory.class);

        bind(ResourceResolver.class)
            .withClassifier(named("applicationResourceResolver"))
            .toProvider(new ResourceResolverProvider("resources"))
            .asSingleton();

        bind(MessageSource.class)
            .withClassifier(named("applicationMessageSource"))
            .toProvider(new MessageSourceProvider("messages"))
            .asSingleton();

        bind(ResourceInjector.class)
            .withClassifier(named("applicationResourceInjector"))
            .to(DefaultApplicationResourceInjector.class)
            .asSingleton();

        bind(ExecutorService.class)
            .withClassifier(named("defaultExecutorService"))
            .toProvider(DefaultExecutorServiceProvider.class)
            .asSingleton();

        bind(UIThreadManager.class)
            .to(DefaultUIThreadManager.class)
            .asSingleton();

        bind(MVCGroupConfigurationFactory.class)
            .to(DefaultMVCGroupConfigurationFactory.class)
            .asSingleton();

        bind(MVCGroupFactory.class)
            .to(DefaultMVCGroupFactory.class)
            .asSingleton();

        bind(MVCGroupManager.class)
            .to(DefaultMVCGroupManager.class)
            .asSingleton();

        for (Lifecycle lifecycle : Lifecycle.values()) {
            bind(LifecycleHandler.class)
                .withClassifier(named(lifecycle.getName()))
                .toProvider(new LifecycleHandlerProvider(lifecycle.getName()))
                .asSingleton();
        }

        bind(WindowManager.class)
            .to(NoopWindowManager.class)
            .asSingleton();

        bind(ActionManager.class)
            .to(DefaultActionManager.class)
            .asSingleton();

        bind(ActionFactory.class)
            .to(DefaultActionFactory.class)
            .asSingleton();

        bind(ActionMetadataFactory.class)
            .to(DefaultActionMetadataFactory.class)
            .asSingleton();

        bind(ArtifactManager.class)
            .to(DefaultArtifactManager.class)
            .asSingleton();

        bind(ArtifactHandler.class)
            .to(ModelArtifactHandler.class)
            .asSingleton();

        bind(ArtifactHandler.class)
            .to(ViewArtifactHandler.class)
            .asSingleton();

        bind(ArtifactHandler.class)
            .to(ControllerArtifactHandler.class)
            .asSingleton();

        bind(ArtifactHandler.class)
            .to(ServiceArtifactHandler.class)
            .asSingleton();

        bind(PlatformHandler.class)
            .toProvider(PlatformHandlerProvider.class)
            .asSingleton();

        bind(AddonManager.class)
            .to(DefaultAddonManager.class)
            .asSingleton();

        bind(EventHandler.class)
            .to(DefaultEventHandler.class)
            .asSingleton();

        bind(ExceptionHandler.class)
            .toProvider(GriffonExceptionHandlerProvider.class)
            .asSingleton();
        // end::bindings[]
    }
}
