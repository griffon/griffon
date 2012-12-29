/*
 * Copyright 2012 the original author or authors.
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

package org.codehaus.griffon.runtime.logging;

import griffon.core.ApplicationHandler;
import griffon.core.GriffonApplication;
import griffon.util.logging.LogManager;
import groovy.lang.Closure;
import groovy.util.ConfigObject;

/**
 * @author Andres Almiray
 * @since 1.2.0
 */
public class DefaultLogManager implements LogManager, ApplicationHandler {
    private final GriffonApplication app;

    public DefaultLogManager(GriffonApplication app) {
        this.app = app;
    }

    public GriffonApplication getApp() {
        return app;
    }

    public void configure(ConfigObject config) {
        Object log4jConfig = app.getConfig().get("log4j");
        if (log4jConfig instanceof Closure) {
            org.apache.log4j.LogManager.resetConfiguration();
            new Log4jConfig().configure((Closure) log4jConfig);
        }
    }
}
