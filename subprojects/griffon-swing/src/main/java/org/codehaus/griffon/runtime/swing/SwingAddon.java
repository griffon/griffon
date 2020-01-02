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
package org.codehaus.griffon.runtime.swing;

import griffon.annotations.core.Nonnull;
import griffon.annotations.core.Nullable;
import griffon.core.CallableWithArgs;
import griffon.core.GriffonApplication;
import griffon.core.GriffonExceptionHandler;
import org.codehaus.griffon.runtime.core.addon.AbstractGriffonAddon;
import org.jdesktop.swinghelper.debug.CheckThreadViolationRepaintManager;
import org.jdesktop.swinghelper.debug.EventDispatchThreadHangMonitor;

import javax.inject.Named;
import javax.swing.RepaintManager;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
@Named("swing")
public class
    SwingAddon extends AbstractGriffonAddon {
    private static final String SWING_EDT_VIOLATIONS_KEY = "griffon.swing.edt.violations.check";
    private static final String SWING_EDT_HANG_MONITOR_KEY = "griffon.swing.edt.hang.monitor";
    private static final String SWING_EDT_HANG_MONITOR_TIMEOUT_KEY = "griffon.swing.edt.hang.monitor.timeout";

    private static final String[] EXCLUDED_PACKAGES =
        System.getProperty("groovy.sanitized.stacktraces",
            "groovy.," +
                "org.codehaus.groovy.," +
                "java.," +
                "javax.," +
                "sun.," +
                "gjdk.groovy.," +
                CheckThreadViolationRepaintManager.class.getPackage().getName()
        ).split("(\\s|,)+");

    @Override
    public void init(@Nonnull GriffonApplication application) {
        String value = System.getProperty(SWING_EDT_VIOLATIONS_KEY);
        if (value != null && Boolean.parseBoolean(value)) {
            if (getLog().isInfoEnabled()) {
                getLog().info("EDT violations check enabled.");
            }

            RepaintManager.setCurrentManager(new CheckThreadViolationRepaintManager(new RepaintManager()));

            GriffonExceptionHandler.addClassTest(new CallableWithArgs<Boolean>() {
                @Override
                @Nullable
                public Boolean call(@Nullable Object... args) {
                    String className = (String) args[0];
                    for (String groovyPackage : EXCLUDED_PACKAGES) {
                        if (className.startsWith(groovyPackage)) {
                            return false;
                        }
                    }
                    return true;
                }
            });
        }

        value = System.getProperty(SWING_EDT_HANG_MONITOR_KEY);
        if (value != null && Boolean.parseBoolean(value)) {
            if (getLog().isInfoEnabled()) {
                getLog().info("EDT hang monitor enabled.");
            }
            EventDispatchThreadHangMonitor.initMonitoring();
            value = System.getProperty(SWING_EDT_HANG_MONITOR_TIMEOUT_KEY);
            if (value != null) {
                try {
                    EventDispatchThreadHangMonitor.getInstance().setTimeout(Long.parseLong(value));
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
        }
    }
}
