/*
 * Copyright 2008-2011 the original author or authors.
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
package griffon.util;

import java.util.Map;
import java.util.Arrays;
import griffon.core.GriffonApplication;
import org.codehaus.groovy.runtime.StackTraceUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Catches and sanitizes all uncaught exceptions.
 *
 * @author Danno Ferrin
 * @author Andres Almiray
 */
public class GriffonExceptionHandler implements Thread.UncaughtExceptionHandler {
    /** "griffon.full.stacktrace" */
    public static final String GRIFFON_FULL_STACKTRACE = "griffon.full.stacktrace";
    /** "griffon.exception.output" */
    public static final String GRIFFON_EXCEPTION_OUTPUT = "griffon.exception.output";

    private static final Logger LOG = LoggerFactory.getLogger(GriffonExceptionHandler.class);
    private static final String[] CONFIG_OPTIONS = {
        GRIFFON_FULL_STACKTRACE,
        GRIFFON_EXCEPTION_OUTPUT
    };

    public void uncaughtException(Thread t, Throwable e) {
        handle(e);
    }

    public void handle(Throwable throwable) {
        try {
            sanitize(throwable);
            if(isOutputEnabled()) throwable.printStackTrace(System.err);
            GriffonApplication app = ApplicationHolder.getApplication();
            if(app != null) {
                LOG.error("Uncaught Exception", throwable);
                app.event("Uncaught" + GriffonNameUtils.getShortName(throwable.getClass()), Arrays.asList(throwable));
                app.event(GriffonApplication.Event.UNCAUGHT_EXCEPTION_THROWN.getName(), Arrays.asList(throwable));
            }
         } catch (Throwable t) {
            sanitize(t);
            if(isOutputEnabled()) t.printStackTrace(System.err);
            LOG.error("An error occured while handling uncaught exception " + throwable, t);
        }
    }

    private void sanitize(Throwable throwable) {
        try {
            if(!Boolean.getBoolean(GRIFFON_FULL_STACKTRACE)) StackTraceUtils.deepSanitize(throwable);
        } catch (Throwable t) {
            // don't let the exception get thrown out, will cause infinite looping!
        }
    }

    public static boolean isOutputEnabled() {
        return Boolean.getBoolean(GRIFFON_EXCEPTION_OUTPUT);
    }

    public static void configure(Map config) {
        for(String option : CONFIG_OPTIONS) {
            if(config.containsKey(option)) {
                System.setProperty(option, String.valueOf(config.get(option)));
            }
        }
    }

    public static void registerExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new GriffonExceptionHandler());
        System.setProperty("sun.awt.exception.handler", GriffonExceptionHandler.class.getName());
    }
}