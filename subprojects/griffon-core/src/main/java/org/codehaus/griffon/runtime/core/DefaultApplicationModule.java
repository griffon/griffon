/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.griffon.runtime.core;

import griffon.core.*;
import griffon.core.addon.AddonManager;
import griffon.core.artifact.ArtifactHandler;
import griffon.core.artifact.ArtifactManager;
import griffon.core.controller.ActionManager;
import griffon.core.env.Lifecycle;
import griffon.core.event.EventHandler;
import griffon.core.event.EventRouter;
import griffon.core.i18n.MessageSource;
import griffon.core.mvc.MVCGroupManager;
import griffon.core.resources.ResourceHandler;
import griffon.core.resources.ResourceInjector;
import griffon.core.resources.ResourceResolver;
import griffon.core.threading.UIThreadManager;
import griffon.core.view.WindowManager;
import griffon.util.CompositeResourceBundleBuilder;
import org.codehaus.griffon.runtime.core.addon.DefaultAddonManager;
import org.codehaus.griffon.runtime.core.artifact.*;
import org.codehaus.griffon.runtime.core.controller.NoopActionManager;
import org.codehaus.griffon.runtime.core.event.DefaultEventHandler;
import org.codehaus.griffon.runtime.core.event.DefaultEventRouter;
import org.codehaus.griffon.runtime.core.i18n.MessageSourceProvider;
import org.codehaus.griffon.runtime.core.injection.AbstractModule;
import org.codehaus.griffon.runtime.core.mvc.DefaultMVCGroupManager;
import org.codehaus.griffon.runtime.core.resources.DefaultApplicationResourceInjector;
import org.codehaus.griffon.runtime.core.resources.DefaultResourceHandler;
import org.codehaus.griffon.runtime.core.resources.ResourceResolverProvider;
import org.codehaus.griffon.runtime.core.threading.DefaultUIThreadManager;
import org.codehaus.griffon.runtime.core.view.NoopWindowManager;
import org.codehaus.griffon.runtime.util.DefaultCompositeResourceBundleBuilder;
import org.codehaus.griffon.runtime.util.ResourceBundleProvider;

import javax.inject.Named;
import java.util.ResourceBundle;

import static griffon.util.AnnotationUtils.named;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
@Named("core")
public class DefaultApplicationModule extends AbstractModule {
    @Override
    protected void doConfigure() {
        classloader();
        applicationConfigurer();
        resourceHandler();
        resourceBundleBuilder();
        applicationConfiguration();
        executorServiceManager();
        eventRouter();
        resourceResolver();
        messageSource();
        resourcesInjector();
        uiThreadManager();
        mvcGroupManager();
        lifecycleHandlers();
        windowManager();
        actionManager();
        artifactManager();
        modelArtifactHandler();
        viewArtifactHandler();
        controllerArtifactHandler();
        serviceArtifactHandler();
        platformHandler();
        addonManager();
        applicationEventHandler();
        exceptionHandler();
    }

    protected void classloader() {
        bind(ApplicationClassLoader.class)
            .to(DefaultApplicationClassLoader.class)
            .asSingleton();
    }

    protected void applicationConfigurer() {
        bind(ApplicationConfigurer.class)
            .to(DefaultApplicationConfigurer.class)
            .asSingleton();
    }

    protected void resourceHandler() {
        bind(ResourceHandler.class)
            .to(DefaultResourceHandler.class)
            .asSingleton();
    }

    protected void resourceBundleBuilder() {
        bind(CompositeResourceBundleBuilder.class)
            .to(DefaultCompositeResourceBundleBuilder.class)
            .asSingleton();
    }

    protected void applicationConfiguration() {
        bind(ResourceBundle.class)
            .withClassifier(named("applicationResourceBundle"))
            .toProvider(new ResourceBundleProvider("Config"))
            .asSingleton();

        bind(ApplicationConfiguration.class)
            .to(DefaultApplicationConfiguration.class)
            .asSingleton();
    }

    protected void executorServiceManager() {
        bind(ExecutorServiceManager.class)
            .to(DefaultExecutorServiceManager.class)
            .asSingleton();
    }

    protected void eventRouter() {
        bind(EventRouter.class)
            .withClassifier(named("applicationEventRouter"))
            .to(DefaultEventRouter.class)
            .asSingleton();

        bind(EventRouter.class)
            .to(DefaultEventRouter.class);
    }

    protected void resourceResolver() {
        bind(ResourceResolver.class)
            .withClassifier(named("applicationResourceResolver"))
            .toProvider(new ResourceResolverProvider("resources"))
            .asSingleton();
    }

    protected void messageSource() {
        bind(MessageSource.class)
            .withClassifier(named("applicationMessageSource"))
            .toProvider(new MessageSourceProvider("messages"))
            .asSingleton();
    }

    protected void resourcesInjector() {
        bind(ResourceInjector.class)
            .withClassifier(named("applicationResourceInjector"))
            .to(DefaultApplicationResourceInjector.class)
            .asSingleton();
    }

    protected void uiThreadManager() {
        bind(UIThreadManager.class)
            .to(DefaultUIThreadManager.class)
            .asSingleton();
    }

    private void mvcGroupManager() {
        bind(MVCGroupManager.class)
            .to(DefaultMVCGroupManager.class)
            .asSingleton();
    }

    private void lifecycleHandlers() {
        for (Lifecycle lifecycle : Lifecycle.values()) {
            bind(LifecycleHandler.class)
                .withClassifier(named(lifecycle.getName()))
                .toProvider(new LifecycleHandlerProvider(lifecycle.getName()))
                .asSingleton();
        }
    }

    private void windowManager() {
        bind(WindowManager.class)
            .to(NoopWindowManager.class)
            .asSingleton();
    }

    private void actionManager() {
        bind(ActionManager.class)
            .to(NoopActionManager.class)
            .asSingleton();
    }

    private void artifactManager() {
        bind(ArtifactManager.class)
            .to(DefaultArtifactManager.class)
            .asSingleton();
    }

    private void modelArtifactHandler() {
        bind(ArtifactHandler.class)
            .to(ModelArtifactHandler.class)
            .asSingleton();
    }

    private void viewArtifactHandler() {
        bind(ArtifactHandler.class)
            .to(ViewArtifactHandler.class)
            .asSingleton();
    }

    private void controllerArtifactHandler() {
        bind(ArtifactHandler.class)
            .to(ControllerArtifactHandler.class)
            .asSingleton();
    }

    private void serviceArtifactHandler() {
        bind(ArtifactHandler.class)
            .to(ServiceArtifactHandler.class)
            .asSingleton();
    }

    private void platformHandler() {
        bind(PlatformHandler.class)
            .toProvider(PlatformHandlerProvider.class)
            .asSingleton();
    }

    private void addonManager() {
        bind(AddonManager.class)
            .to(DefaultAddonManager.class)
            .asSingleton();
    }

    private void applicationEventHandler() {
        bind(EventHandler.class)
            .to(DefaultEventHandler.class)
            .asSingleton();
    }

    private void exceptionHandler() {
        bind(GriffonExceptionHandler.class)
            .asSingleton();
    }
}
