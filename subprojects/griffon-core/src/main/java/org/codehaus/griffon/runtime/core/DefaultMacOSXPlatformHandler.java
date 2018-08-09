/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2008-2018 the original author or authors.
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

import griffon.annotations.core.Nonnull;
import griffon.core.GriffonApplication;
import griffon.util.GriffonApplicationUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static java.util.Collections.singletonList;

/**
 * Handles OSX integration.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public class DefaultMacOSXPlatformHandler extends DefaultPlatformHandler {
    @Override
    public void handle(@Nonnull GriffonApplication application) {
        super.handle(application);

        // use unified menu bar
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        try {
            addEventHandlers(application);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        /*
        // set menu bar title
        String title = application.getConfiguration().getAsString("application.title", "Griffon");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", capitalize(title));
        */
    }

    private void addEventHandlers(@Nonnull GriffonApplication application) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
        InvocationTargetException, InstantiationException {

        Class<?> applicationClass = Class.forName("com.apple.eawt.Application");
        Class<?> quitHandlerClass;
        Class<?> aboutHandlerClass;
        Class<?> preferencesHandlerClass;

        if (GriffonApplicationUtils.isJdk9()) {
            quitHandlerClass = Class.forName("java.awt.desktop.QuitHandler");
            aboutHandlerClass = Class.forName("java.awt.desktop.AboutHandler");
            preferencesHandlerClass = Class.forName("java.awt.desktop.PreferencesHandler");
        } else {
            quitHandlerClass = Class.forName("com.apple.eawt.QuitHandler");
            aboutHandlerClass = Class.forName("com.apple.eawt.AboutHandler");
            preferencesHandlerClass = Class.forName("com.apple.eawt.PreferencesHandler");
        }

        Object app = applicationClass.getConstructor((Class[]) null).newInstance((Object[]) null);
        Object proxy = Proxy.newProxyInstance(DefaultMacOSXPlatformHandler.class.getClassLoader(), new Class<?>[]{
            quitHandlerClass, aboutHandlerClass, preferencesHandlerClass}, new PlatformInvocationHandler(application));

        boolean skipAbout = application.getConfiguration().getAsBoolean("osx.noabout", false);
        boolean skipPrefs = application.getConfiguration().getAsBoolean("osx.noprefs", false);

        applicationClass.getDeclaredMethod("setQuitHandler", quitHandlerClass).invoke(app, proxy);
        if (!skipAbout) {
            applicationClass.getDeclaredMethod("setAboutHandler", aboutHandlerClass).invoke(app, proxy);
        }
        if (!skipPrefs) {
            applicationClass.getDeclaredMethod("setPreferencesHandler", preferencesHandlerClass).invoke(app, proxy);
        }
    }

    private static class PlatformInvocationHandler implements InvocationHandler {
        private final GriffonApplication application;

        private PlatformInvocationHandler(@Nonnull GriffonApplication application) {
            this.application = application;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("Intercepted " + method.getName());
            boolean skipQuit = application.getConfiguration().getAsBoolean("osx.noquit", false);

            if ("handleQuitRequestWith".equals(method.getName())) {
                if (skipQuit) {
                    application.getEventRouter().publishEvent("OSXQuit", singletonList(application));
                } else {
                    application.shutdown();
                }
            } else if ("handleAbout".equals(method.getName())) {
                application.getEventRouter().publishEvent("OSXAbout", singletonList(application));
            } else if ("handlePreferences".equals(method.getName())) {
                application.getEventRouter().publishEvent("OSXPrefs", singletonList(application));
            }
            return null;
        }
    }
}