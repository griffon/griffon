/*
 * Copyright 2009-2012 the original author or authors.
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

import griffon.core.GriffonApplication;
import griffon.util.ApplicationHolder;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import java.util.Arrays;


/**
 * A Log4j Appender that triggers Griffon application events.
 *
 * @author Andres Almiray
 * @since 0.9.3
 */
public class GriffonApplicationEventAppender extends AppenderSkeleton {
    private static final String EVENT_NAME = "LogEvent";

    public GriffonApplicationEventAppender(Layout layout) {
        this.layout = layout;
    }

    @Override
    protected void append(LoggingEvent event) {
        Throwable throwable = null;
        ThrowableInformation throwableInfo = event.getThrowableInformation();
        if (throwableInfo != null) {
            throwable = throwableInfo.getThrowable();
        }
        fireApplicationEvent(event.getLevel(), asString(event), throwable);
    }

    private String asString(LoggingEvent event) {
        StringBuilder builder = new StringBuilder(layout.format(event));

        if (layout.ignoresThrowable()) {
            String[] s = event.getThrowableStrRep();
            if (s != null) {
                int len = s.length;
                for (int i = 0; i < len; i++) {
                    builder.append(s[i]);
                    builder.append(Layout.LINE_SEP);
                }
            }
        }

        return builder.toString();
    }

    public void close() {
    }

    public boolean requiresLayout() {
        return true;
    }

    private void fireApplicationEvent(Level level, String message, Throwable throwable) {
        GriffonApplication app = ApplicationHolder.getApplication();
        if (app != null) {
            app.eventAsync(EVENT_NAME, Arrays.asList(level.toString(), message, throwable));
        }
    }
}
